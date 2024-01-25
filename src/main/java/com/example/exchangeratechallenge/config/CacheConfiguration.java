package com.example.exchangeratechallenge.config;

import org.springframework.cache.CacheManager;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CacheConfiguration {

    @Bean
    public CacheManager cacheManager(){
        return new ConcurrentMapCacheManager("exchangeRates","allExchangeRates","currencyConversions","multiCurrencyConversions");
    }
}
