package com.ny.caffeinespring.service;

import com.ny.caffeinespring.config.MyFirstCacheManager;
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

    public Object getData(){
        Cache cache = firstCacheManager.getCache(MyFirstCacheManager.Caches.five_min_cache.name());
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
