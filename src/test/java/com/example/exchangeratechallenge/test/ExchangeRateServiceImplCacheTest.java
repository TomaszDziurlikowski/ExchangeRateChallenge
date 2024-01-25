package com.example.exchangeratechallenge.test;

import com.example.exchangeratechallenge.service.ExchangeRateServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.cache.CacheManager;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class ExchangeRateServiceImplCacheTest {
    private ExchangeRateServiceImpl exchangeRateService;

    @Mock
    private RestTemplate restTemplate;

    private CacheManager cacheManager;

    @BeforeEach
    void setUp() {
        restTemplate = mock(RestTemplate.class);
        cacheManager = new ConcurrentMapCacheManager("exchangeRates");
        exchangeRateService = new ExchangeRateServiceImpl(restTemplate, cacheManager);
    }

    @Test
    void whenCalledTwice_thenSecondCallShouldUseCache() {
        String from = "USD";
        String to = "EUR";
        BigDecimal rate = BigDecimal.valueOf(0.85);

        Map<String, Object> apiResponse = new HashMap<>();
        apiResponse.put("rates", Map.of(to, rate));
        when(restTemplate.getForObject(anyString(), eq(Map.class))).thenReturn(apiResponse);

        // First call - should use the RestTemplate
        BigDecimal firstCallRate = exchangeRateService.getExchangeRate(from, to);
        assertEquals(rate, firstCallRate);

        // Reset the mock to not return any value on subsequent calls
        reset(restTemplate);

        // Second call - should use the cache and not call the RestTemplate
        BigDecimal secondCallRate = exchangeRateService.getExchangeRate(from, to);
        assertEquals(rate, secondCallRate);

        // Verify RestTemplate was called only once
        verify(restTemplate, times(1)).getForObject(anyString(), eq(Map.class));
    }
}
