package com.example.exchangeratechallenge.cache;

import org.springframework.cache.CacheManager;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class CacheScheduler {

    private final CacheManager cacheManager;
    public CacheScheduler(CacheManager cacheManager){
        this.cacheManager = cacheManager;
    }

    @Scheduled(fixedRate = 60000)
    public void evictAllCachesInterval(){
        cacheManager.getCacheNames().forEach(cacheName -> cacheManager.getCache(cacheName).clear());
    }
}
