
# caffeine
    
### 官网：https://github.com/ben-manes/caffeine/blob/master/README.md

### 使用
引入jar包

    <dependency>
        <groupId>com.github.ben-manes.caffeine</groupId>
        <artifactId>caffeine</artifactId>
        <version>2.9.3</version>
    </dependency>

### 回收策略
 Window TinyLfu：缓存命中率极高
			缓存的删除策略使用的是惰性删除和定时删除，但是我也可以自己调用cache.cleanUp()方法手动触发一次回收操作

 lfu：最不经常使用（最少次数）

 lru：最近最少访问（最长时间没有没访问）

### 填充策略
 访问缓存中没有数据，需要添加数据到缓存中时的三种策略

#### 1.手动添加
 **get方法执行是原子性的，即多个线程同时请求该值，填充方法只会执行一次，所以get方法要优于getIfPresent**

	 Cache<String, Object> manualCache = Caffeine.newBuilder()
	         .expireAfterWrite(10, TimeUnit.MINUTES)
	         .maximumSize(10_000)
	         .build();
	 graph = manualCache.get(key, k -> createExpensiveGraph(k));

#### 2.同步自动添加
 **直接调用get方法，如果没值会调用createExpensiveGraph()方法填充，其本质是调用了CacheLoader的load方法**

     LoadingCache<String, Object> loadingCache = Caffeine.newBuilder()
	         .maximumSize(10_000)
	         .expireAfterWrite(10, TimeUnit.MINUTES)
	         .build(key -> createExpensiveGraph(key));
	 Object graph = loadingCache.get(key);`

     LoadingCache<String, Object> loadingCache = Caffeine.newBuilder()
         .maximumSize(10_000)
         .expireAfterWrite(10, TimeUnit.MINUTES)
         //本质是调用了load方法
         .build(new CacheLoader<String, Long>() {
             @Override
             public @Nullable Long load(@NonNull String key) throws Exception {
                 return 400L;
             }
         });

#### 3.异步自动添加

	 AsyncLoadingCache<String, Object> asyncLoadingCache = Caffeine.newBuilder()
	             .maximumSize(10_000)
	             .expireAfterWrite(10, TimeUnit.MINUTES)
	             // Either: Build with a synchronous computation that is wrapped as asynchronous
	             .buildAsync(key -> createExpensiveGraph(key));
	 CompletableFuture<Object> graph = asyncLoadingCache.get(key);

### 驱逐策略
#### 1.基于大小
 a)基于缓存大小

 b)基于权重

 表示权重大于10时触发删除

	 LoadingCache<String, DataObject> cache = Caffeine.newBuilder()
	   .maximumWeight(10)
	   .weigher((k,v) -> 5)
	   .build(k -> DataObject.get("Data for " + k));

#### 2.基于时间
 a)expireAfterAccess：基于最后一次写入或者访问开始倒计时，假如一直有请求访问该key，则一直不会过期

 b)expireAfterWrite：最后一次写入缓存开始倒计时

 c)expireAfter：自定义策略，过期时间由Expiry实现独自计算

#### 3.基于引用
 强引用 > 软引用 > 弱引用 > 虚引用

	 引用类型	被垃圾回收时间	        用途			生存时间
	 强引用		从来不会			对象的一般状态	        JVM停止运行时终止
	 软引用		在内存不足时		对象缓存			内存不足时终止
	 弱引用		在垃圾回收时		对象缓存			c运行后终止
	 虚引用		Unknown			Unknown			Unknown

### 移除监听
 移除：

 驱逐（eviction）：由于满足了某种驱逐策略，后台自动进行的删除操作

 无效（invalidation）：表示由调用方手动删除缓存

 移除（removal）：监听驱逐或无效操作的监听器

 可在移除后记录信息，key value及移除的原因
		Cache<Key, Graph> graphs = Caffeine.newBuilder()
		    .removalListener((Key key, Graph graph, RemovalCause cause) ->
		        System.out.printf("Key %s was removed (%s)%n", key, cause))
		    .build();

### 手动删除缓存

	 // individual key
	 cache.invalidate(key)
	 // individual keys
	 cache.invalidateAll(keys)
	 // all keys
	 cache.invalidateAll()

### 统计
	 Cache<Key, Graph> cache = Caffeine.newBuilder()
	     .maximumSize(10_000)
	     .recordStats()
	     .build();
	    cache.stats() 

 hitRate()：返回命中与请求的比率

 hitCount(): 返回命中缓存的总数

 evictionCount()：缓存逐出的数量

 averageLoadPenalty()：加载新值所花费的平均时间


### 刷新（Refresh）
 这个参数是 LoadingCache 和 AsyncLoadingCache 的才会有的

 refreshAfterWrite 多少秒之后刷新数据，是惰性刷新的，即数据其实已经过期了，但是没有访问该条数据的时候，存的还是旧值

 当访问的key在缓存中不存在，会触发load方法，往缓存中放入值

 当时间已经过期，并且触发访问后，会先返回旧值，然后调用reload()方法，更新缓存


### 策略（Policy）

### 测试

### FAQ:
	 1.基于权重驱逐策略---DONE
	 2.Refresh什么作用---DONE
	 3.回收策略 Window TinyLfu
	 4.异步填充


## CacheManager

### 概念

- 1.JSR是Java Specification Requests的缩写，意思是Java 规范提案；JSR 107是JCache API的首个早期草案

    - 1.CachingProvider定义了创建、配置、获取、管理和控制多个CacheManager。一个应用可以在运行期访问多个CachingProvider。

    - 2.CacheManager定义了创建、配置、获取、管理和控制多个唯一命名的Cache，这些Cache存在于CacheManager的上下文中。一个CacheManager仅被一个CachingProvider所拥有。

    - 3.Cache是一个类似Map的数据结构并临时存储以Key为索引的值。一个Cache仅被一个CacheManager所拥有。

    - 4.Entry是一个存储在Cache中的key-value对。

    - 5.Expiry 每一个存储在Cache中的条目有一个定义的有效期。一旦超过这个时间，条目为过期的状态。一旦过期，条目将不可访问、更新和删除。缓存有效期可以通过ExpiryPolicy设置。
    - 
- 2.spring支持JSR107，基本概念
  - Cache	缓存接口，定义缓存操作。实现有：RedisCache、EhCacheCache、ConcurrentMapCache等
  - CaheManager	缓存管理器，管理各种缓存（Cache组件）
  - @Cacheable	主要针对方法配置，能够根据方法的请求参数对其结果进行缓存
  - @CacheEvict	清空缓存（和==@Cacheable==配合使用才有意义）
  - @CacheEvict	清空缓存（和==@Cacheable==配合使用才有意义）
  - @CachePut	保证方法被调用，又希望结果被缓存。（和==@Cacheable==配合使用才有意义）
  - @Caching	组合注解，可同时使用上面三个注解。用于实现复杂的缓存策略
  - @CacheConfig	一般用在类上，抽取配置其他注解的共有属性，例如cacheNames
  - @EnableCaching	开启基于注解的缓存

- 3.@Cacheable/@CachePut/@CacheEvict注解下的常用属性
  - cacheNames	缓存的名称，在 spring 配置文件中定义，必须指定至少一个	例如： @Cacheable(cacheNames=”mycache”) 或者 @Cacheable(cacheNames={”cache1”,”cache2”}
  - value	等效于cacheNames	例如： @Cacheable(value=”mycache”) 或者 @Cacheable(value={”cache1”,”cache2”}
  - key	缓存的 key，可以为空，如果指定要按照 SpEL 表达式编写，如果不指定，则默认使用SimpleKeyGenerator策略生成key	例如： @Cacheable(value=”testcache”,key=”#userName”)
  - keyGenerator	指定key的生成策略	例如：keyGenerator = “myKeyGenerator”
  - condition	缓存的条件，可以为空，使用 SpEL 编写，返回 true 或者 false，只有为 true 才进行缓存/清除缓存，在调用方法之前之后都能判断	例如： @Cacheable(value=”testcache”,condition=”#userName.length()>2”)
  - allEntries (@CacheEvict )	是否清空所有缓存内容，缺省为 false，如果指定为 true，则方法调用后将立即清空所有缓存 （指定CacheName下的缓存）	例如： @CachEvict(value=”testcache”,allEntries=true)
  - beforeInvocation (@CacheEvict)	是否在方法执行前就清空，缺省为 false，如果指定为 true，则在方法还没有执行的时候就清空缓存，缺省情况下，如果方法执行抛出异常，则不会清空缓存	例如： @CachEvict(value=”testcache”，beforeInvocation=true)
  - unless (@CachePut) (@Cacheable)	用于否决缓存的，不像condition，该表达式只在方法执行之后判断，此时可以拿到返回值result进行判断。条件为true不会缓存，fasle才缓存	例如： @Cacheable(value=”testcache”,unless=”#result == null”)
    
- 4.SpEL表达式详解
  - methodName	root.object	当前被调用的方法名	#root.methodName
  - method	root.object	当前被调用的方法	#root.method.name
  - target	root.object	当前被调用的目标对象	#root.target
  - targetClass	root.object	当前被调用的目标对象类	#root.targetClass
  - args	root.object	当前被调用的方法的参数列表	#root.args[0]
  - caches	root object	当前方法调用使用的缓存列表（如@Cacheable(value={“cache1”, “cache2”})），则有两个cache	#root.caches[0].name
  - argument name	evaluation context	方法参数的名字. 可以直接==#参数名==，也可以使用 #p0或==#a0== 的形式，0代表参数的索引；	#iban 、 #a0 、 #p0
  - result	evaluation context	方法执行后的返回值（仅当方法执行之后的判断有效，如‘unless’，’cache put’的表达式 ’cache evict’的表达式beforeInvocation=false）	#result


### 使用
1.引入依赖

	<dependency>
		<groupId>org.springframework.boot</groupId>
		<artifactId>spring-boot-starter-cache</artifactId>
	</dependency>


2.开启cache

    @EnableCaching


### 默认实现

- 1.只指定以上配置，springboot启动会自动生成一个cacheManager，配置类为`SimpleCacheConfiguration`，cacheManager类型为`ConcurrentMapCacheManager`
  - 1.入口：`CacheAutoConfiguration`为Cache配置类，项目启动会加载该类
  - 2.其中静态内部类`CacheConfigurationImportSelector`，实现了`ImportSelector`，启动会自动注入`SimpleCacheConfiguration`
- 2.`ConcurrentMapCacheManager`用于管理Cache，调用`getCache()`方法，如果没有配置Cache，则会调用`createConcurrentMapCache`自动生成一个Cache，类型为`ConcurrentMapCache`


### 日常用法

- 1.新建配置类`MyFirstCacheManager`
- 2.通过定义枚举来定义不同的Cache
- 3.`@Bean`注入CacheManager
- 4.将Cache交给CacheManager管理
- 5.使用：
  - 1.可直接注入`firstCacheManager`，通过`getCache()`方法获取对应的Cache
  - 2.使用在方法上，通过`@Cacheable`注解的`cacheNames`属性置顶对应的Cache
  

	//直接使用
	@Resource
	private CacheManager firstCacheManager;

    public Object getData(){
    Cache cache = firstCacheManager.getCache(MyFirstCacheManager.Caches.five_min_cache.name());
    Cache.ValueWrapper abc = cache.get("abc");
    return abc;
    }

	//用在方法上
	@Cacheable(cacheNames = "five_min_cache",key = "#random")


