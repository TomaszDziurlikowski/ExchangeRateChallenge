package com.example.exchangeratechallenge.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.cache.CacheManager;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class ExchangeRateServiceImplTest {
    private RestTemplate restTemplate;
    private ExchangeRateServiceImpl service;

    private CacheManager cacheManager;

    @BeforeEach
    public void setUp() {
        restTemplate = mock(RestTemplate.class);
        service = new ExchangeRateServiceImpl(restTemplate, cacheManager);
    }

    @Test
    public void testGetExchangeRate() {
        // Arrange
        String from = "USD";
        String to = "EUR";
        BigDecimal expectedRate = BigDecimal.valueOf(0.85);
        Map<String, Object> apiResponse = new HashMap<>();
        apiResponse.put("quotes", Map.of(to, expectedRate));
        when(restTemplate.getForObject(anyString(), eq(Map.class))).thenReturn(apiResponse);

        // Act
        BigDecimal rate = service.getExchangeRate(from, to);

        // Assert
        assertNotNull(rate);
        assertEquals(expectedRate, rate);
        verify(restTemplate).getForObject(anyString(), eq(Map.class));
    }

    @Test
    public void testGetAllExchangeRates() {
        // Arrange
        String base = "USD";
        Map<String, BigDecimal> expectedRates = Map.of("EUR", BigDecimal.valueOf(0.85), "JPY", BigDecimal.valueOf(110.25));
        Map<String, Object> apiResponse = new HashMap<>();
        apiResponse.put("rates", expectedRates);
        when(restTemplate.getForObject(anyString(), eq(Map.class))).thenReturn(apiResponse);

        // Act
        Map<String, BigDecimal> rates = service.getAllExchangeRates(base);

        // Assert
        assertNotNull(rates);
        assertEquals(expectedRates, rates);
        verify(restTemplate).getForObject(anyString(), eq(Map.class));
    }

    @Test
    public void testConvertCurrency() {
        // Arrange
        String from = "USD";
        String to = "EUR";
        BigDecimal amount = BigDecimal.valueOf(100);
        BigDecimal expectedConvertedAmount = BigDecimal.valueOf(85);
        Map<String, Object> apiResponse = new HashMap<>();
        apiResponse.put("result", expectedConvertedAmount);
        when(restTemplate.getForObject(anyString(), eq(Map.class))).thenReturn(apiResponse);

        // Act
        BigDecimal convertedAmount = service.convertCurrency(from, to, amount);

        // Assert
        assertNotNull(convertedAmount);
        assertEquals(expectedConvertedAmount, convertedAmount);
        verify(restTemplate).getForObject(anyString(), eq(Map.class));
    }

    @Test
    public void testConvertToMultipleCurrencies() {
        // Arrange
        String from = "USD";
        List<String> toCurrencies = List.of("EUR", "JPY");

        BigDecimal amount = BigDecimal.valueOf(100);
        Map<String, BigDecimal> expectedResults = Map.of("EUR", BigDecimal.valueOf(85), "JPY", BigDecimal.valueOf(11025));
        when(restTemplate.getForObject(anyString(), eq(Map.class)))
                .thenReturn(createMockResponse("EUR", BigDecimal.valueOf(85)))
                .thenReturn(createMockResponse("JPY", BigDecimal.valueOf(11025)));

        // Act
        Map<String, BigDecimal> results = service.convertToMultipleCurrencies(from, toCurrencies, amount);

        // Assert
        assertNotNull(results);
        assertEquals(expectedResults, results);
        verify(restTemplate, times(2)).getForObject(anyString(), eq(Map.class));
    }

    private Map<String, Object> createMockResponse(String currency, BigDecimal rate) {
        Map<String, Object> response = new HashMap<>();
        response.put("result", rate);
        response.put("query", Map.of("to", currency));
        return response;
    }
}
