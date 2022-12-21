package com.ny.caffeinespring.service;

import com.github.benmanes.caffeine.cache.LoadingCache;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;

/**
 * @author wenan.ren
 * @date 2022/12/20 11:40
 * @Description
 */
@Controller
public class CacheController {

    @Resource
    private LoadingCache<String, Long> caffeineCache;

    private static String allUserCount = "allUserCount";

    @RequestMapping("/testCache")
    @ResponseBody
    public Object test() throws InterruptedException {
//        caffeineCache.put(allUserCount, 100L);
//        Long obj = caffeineCache.get(allUserCount);
        Long obj = caffeineCache.get("abc");

//        Thread.sleep(4000);
//        caffeineCache.stats().hitRate();

//        Long integer = caffeineCache.get(allUserCount);
        return obj;
    }
}
