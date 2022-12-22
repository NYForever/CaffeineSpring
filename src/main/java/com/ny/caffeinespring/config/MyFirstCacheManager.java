package com.ny.caffeinespring.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author wenan.ren
 * @date 2022/12/22 16:43
 * @Description
 */
@Configuration
public class MyFirstCacheManager {

    private static final int DEFAULT_MAX_TTL = 10 * 60;
    private static final int DEFAULT_MAXSIZE = 100 * 1000;

    public enum Caches {
        /**
         * 曝光缓存5分钟
         */
        five_min_cache(300),
        ten_min_cache(600);

        private int size = DEFAULT_MAXSIZE;
        private int ttl = DEFAULT_MAX_TTL;

        Caches() {
        }

        Caches(int ttl) {
            this.ttl = ttl;
        }

        Caches(int size, int ttl) {
            this.size = size;
            this.ttl = ttl;
        }

        public int getSize() {
            return size;
        }

        public int getTtl() {
            return ttl;
        }
    }

    @Bean
    public CacheManager firstCacheManager() {
        List<CaffeineCache> caffeines = new ArrayList<>();

        for (Caches value : Caches.values()) {
            caffeines.add(
                    new CaffeineCache(value.name(),
                            Caffeine.newBuilder()
                                    .expireAfterWrite(value.getTtl(),TimeUnit.SECONDS)
                                    .maximumSize(value.getSize())
                                    .recordStats()
                                    .build())
            );
        }
        SimpleCacheManager simpleCacheManager = new SimpleCacheManager();
        simpleCacheManager.setCaches(caffeines);
        return simpleCacheManager;
    }

}
