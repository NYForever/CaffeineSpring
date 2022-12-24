package com.ny.caffeinespring.config;

import com.github.benmanes.caffeine.cache.*;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * @author wenan.ren
 * @date 2022/12/20 11:38
 * @Description
 */
@Configuration
public class CacheConfig {

    @Bean
    public AsyncLoadingCache<String, Long> asynCaffeineCache() {
        return Caffeine.newBuilder()
                .maximumSize(10_000)
                .recordStats()
                .expireAfterWrite(10, TimeUnit.MINUTES)
                .buildAsync(key -> createExpensiveGraph());
    }

    @Bean
    public LoadingCache<String, Long> caffeineCache() {

        return Caffeine.newBuilder()
                .maximumSize(10_000)
                .recordStats()
                .expireAfterWrite(10, TimeUnit.MINUTES)
                .build(new CacheLoader<String, Long>() {
                    @Override
                    public @Nullable Long load(@NonNull String key) throws Exception {
                        return 400L;
                    }
                });

//        return Caffeine.newBuilder()
////                .expireAfterWrite(2 * 60 * 60, TimeUnit.SECONDS)
////                .refreshAfterWrite(2, TimeUnit.SECONDS)
//                .initialCapacity(100)
//                .maximumSize(100)
//                .build(new CacheLoader<String, Long>() {
//                    @Override
//                    public @Nullable Long load(@NonNull String key) throws Exception {
//                        Thread.sleep(3000);
//                        System.out.println("refresh");
//                        return 400L;
//                    }
//                    @Override
//                    public Long reload(@NonNull String key, @NonNull Long oldValue){
//                        System.out.println("reload");
//                        return 500L;
//                    }
//                });
    }

    /**
     * refreshAfterWrite
     *
     * 这个参数是 LoadingCache 和 AsyncLoadingCache 的才会有的
     * refreshAfterWrite 多少秒之后刷新数据，是惰性刷新的，即数据其实已经过期了，但是没有访问该条数据的时候，存的还是旧值
     * 当访问的key在缓存中不存在，会触发load方法，往缓存中放入值
     * 当时间已经过期，并且触发访问后，会先返回旧值，然后调用reload()方法，更新缓存
     *
     * @return
     * @throws InterruptedException
     */
    private Long createExpensiveGraph() throws InterruptedException {
        Thread.sleep(3000);
        System.out.println("asyn");
        return 200L;
    }
}
