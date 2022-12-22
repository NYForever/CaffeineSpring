package com.ny.caffeinespring.controller;

import com.github.benmanes.caffeine.cache.AsyncLoadingCache;
import com.github.benmanes.caffeine.cache.LoadingCache;
import com.ny.caffeinespring.service.CacheService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * @author wenan.ren
 * @date 2022/12/20 11:40
 * @Description
 */
@Controller
public class CacheController {

    @Resource
    private LoadingCache<String, Long> caffeineCache;

    @Resource
    private AsyncLoadingCache<String, Long> asynCaffeineCache;

    @Resource
    private CacheService cacheService;

    private static String allUserCount = "allUserCount";

    @Autowired
    private ApplicationContext applicationContext;

    @RequestMapping("/fiveCacheManager")
    @ResponseBody
    public Object fiveCacheManager() {
        int random = (int) (Math.random() * 10);
        Long aLong = cacheService.fiveCacheManager(random);
        return aLong;
    }

    @RequestMapping("/tenCacheManager")
    @ResponseBody
    public Object tenCacheManager() {
        int random = (int) (Math.random() * 10);
        Long aLong = cacheService.tenCacheManager(random);
        return aLong;
    }

    @RequestMapping("/testCacheManager")
    @ResponseBody
    public Object testCacheManager() {
        int random = (int) (Math.random() * 10);
        Long aLong = cacheService.testCacheManager(random);
        return aLong;
    }



    @RequestMapping("asynCache")
    @ResponseBody
    public Long asynCache() throws ExecutionException, InterruptedException {
        CompletableFuture<Long> future = asynCaffeineCache.get("abc");
        Long aLong;
        try {
            aLong = future.get(2, TimeUnit.SECONDS);
        } catch (TimeoutException e) {
            System.out.println("超时");
            return null;
        }
        return aLong;
    }

    @RequestMapping("/testCache")
    @ResponseBody
    public Object testCache() throws InterruptedException {
//        caffeineCache.put(allUserCount, 100L);
//        Long obj = caffeineCache.get(allUserCount);
        Long obj = caffeineCache.get("abc");

//        Thread.sleep(4000);
//        caffeineCache.stats().hitRate();

//        Long integer = caffeineCache.get(allUserCount);
        return obj;
    }
}
