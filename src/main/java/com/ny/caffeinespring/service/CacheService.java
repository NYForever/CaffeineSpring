package com.ny.caffeinespring.service;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import com.github.benmanes.caffeine.cache.stats.CacheStats;
import com.ny.caffeinespring.config.MyFirstCacheManager;
import org.springframework.boot.autoconfigure.cache.CacheProperties;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author wenan.ren
 * @date 2022/12/22 14:08
 * @Description
 */
@Service
@CacheConfig(cacheNames = "testaaa")
public class CacheService {

    @Resource
    private CacheManager firstCacheManager;

    @Resource
    private LoadingCache<String, Long>  caffeineCache;

    public Object getData(){
        CacheStats stats = caffeineCache.stats();
        stats.hitRate();
        Cache cache = firstCacheManager.getCache(MyFirstCacheManager.Caches.five_min_cache.name());

        com.github.benmanes.caffeine.cache.Cache nativeCache = (com.github.benmanes.caffeine.cache.Cache) firstCacheManager.getCache(MyFirstCacheManager.Caches.five_min_cache.name()).getNativeCache();
        CacheStats stats1 = nativeCache.stats();
        double v = stats1.hitRate();
        long l = stats1.evictionCount();


        Cache.ValueWrapper abc = cache.get("abc");
        return abc;
    }

    @Cacheable(cacheNames = "five_min_cache",key = "#random")
    public Long fiveCacheManager(int random) {
        System.out.println("fiveCacheManager testCache");
        return Long.parseLong(random + "");
    }

    @Cacheable(cacheNames = "ten_min_cache",key = "#random")
    public Long tenCacheManager(int random) {
        System.out.println("tenCacheManager testCache");
        return Long.parseLong(random + "");
    }

    @Cacheable(cacheNames = "cacheService.testCacheManager",key = "#random")
    public Long testCacheManager(int random) {
        System.out.println("service testCache");
        return Long.parseLong(random + "");
    }

}
