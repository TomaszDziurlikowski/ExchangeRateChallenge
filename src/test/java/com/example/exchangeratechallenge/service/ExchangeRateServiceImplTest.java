package com.example.exchangeratechallenge.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.cache.CacheManager;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class ExchangeRateServiceImplTest {
    @Mock
    private RestTemplate restTemplate;

    private ExchangeRateServiceImpl exchangeRateService;

    private CacheManager cacheManager;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        exchangeRateService = new ExchangeRateServiceImpl(restTemplate,cacheManager);
    }

    @Test
    void testGetExchangeRate() {
        // Setup
        String fromCurrency = "USD";
        String toCurrency = "EUR";

        // Mocking the API response
        Map<String, Object> mockedApiResponse = new LinkedHashMap<>();
        mockedApiResponse.put("success", true);
        Map<String, Object> mockedQuotes = new LinkedHashMap<>();
        mockedQuotes.put("USDEUR", 0.85);
        mockedApiResponse.put("quotes", mockedQuotes);

        when(restTemplate.getForObject(anyString(), eq(Map.class))).thenReturn(mockedApiResponse);

        // Execution
        BigDecimal exchangeRate = exchangeRateService.getExchangeRate(fromCurrency, toCurrency);

        // Assertions
        assertNotNull(exchangeRate);
        assertEquals(0, BigDecimal.valueOf(0.85).compareTo(exchangeRate));
    }

    @Test
    void testGetExchangeRateFailure() {
        // Setup
        String fromCurrency = "USD";
        String toCurrency = "EUR";

        // Mocking a failed API response
        Map<String, Object> mockedApiResponse = new LinkedHashMap<>();
        mockedApiResponse.put("success", false);

        when(restTemplate.getForObject(anyString(), eq(Map.class))).thenReturn(mockedApiResponse);

        // Execution and Assertions
        assertThrows(IllegalArgumentException.class, () -> {
            exchangeRateService.getExchangeRate(fromCurrency, toCurrency);
        });
    }

    @Test
    void testGetAllExchangeRates() {
        // Setup
        String baseCurrency = "USD";

        // Mocking the API response
        Map<String, Object> mockedApiResponse = new HashMap<>();
        Map<String, BigDecimal> mockedQuotes = new HashMap<>();
        mockedQuotes.put("USDEUR", BigDecimal.valueOf(0.85));
        mockedQuotes.put("USDGBP", BigDecimal.valueOf(0.75));
        mockedApiResponse.put("quotes", mockedQuotes);

        when(restTemplate.getForObject(anyString(), eq(Map.class))).thenReturn(mockedApiResponse);

        // Execution
        Map<String, BigDecimal> exchangeRates = exchangeRateService.getAllExchangeRates(baseCurrency);

        // Assertions
        assertNotNull(exchangeRates);
        assertFalse(exchangeRates.isEmpty());
        assertEquals(mockedQuotes, exchangeRates);
        assertTrue(exchangeRates.containsKey("USDEUR") && exchangeRates.get("USDEUR").compareTo(BigDecimal.valueOf(0.85)) == 0);
        assertTrue(exchangeRates.containsKey("USDGBP") && exchangeRates.get("USDGBP").compareTo(BigDecimal.valueOf(0.75)) == 0);
    }


    @Test
    void testConvertCurrency() {
        // Setup
        String fromCurrency = "USD";
        String toCurrency = "EUR";
        BigDecimal amount = BigDecimal.valueOf(100);

        // Mocking the API response
        Map<String, Object> mockedApiResponse = new HashMap<>();
        mockedApiResponse.put("result", 85.0); // Assuming the conversion result is 85

        when(restTemplate.getForObject(anyString(), eq(Map.class))).thenReturn(mockedApiResponse);

        // Execution
        BigDecimal conversionResult = exchangeRateService.convertCurrency(fromCurrency, toCurrency, amount);

        // Assertions
        assertNotNull(conversionResult);
        assertEquals(0, BigDecimal.valueOf(85).compareTo(conversionResult));
    }

    @Test
    void testConvertToMultipleCurrencies() {
        // Setup
        String fromCurrency = "USD";
        List<String> toCurrencies = Arrays.asList("EUR", "GBP");
        BigDecimal amount = BigDecimal.valueOf(100);

        // Mocking the API response
        Map<String, Object> mockedApiResponse = new HashMap<>();
        Map<String, BigDecimal> mockedQuotes = new HashMap<>();
        mockedQuotes.put("USDEUR", BigDecimal.valueOf(0.85));
        mockedQuotes.put("USDGBP", BigDecimal.valueOf(0.75));
        mockedApiResponse.put("success", true);
        mockedApiResponse.put("quotes", mockedQuotes);

        when(restTemplate.getForObject(anyString(), eq(Map.class))).thenReturn(mockedApiResponse);

        // Execution
        HashMap<String, BigDecimal> conversionResults = (HashMap<String, BigDecimal>) exchangeRateService.convertToMultipleCurrencies(fromCurrency, toCurrencies, amount);

        // Assertions
        assertNotNull(conversionResults);
        assertEquals(2, conversionResults.size());
        assertTrue(conversionResults.containsKey("EUR"));
        assertTrue(conversionResults.containsKey("GBP"));
        assertEquals(0, BigDecimal.valueOf(85).compareTo(conversionResults.get("EUR")));
        assertEquals(0, BigDecimal.valueOf(75).compareTo(conversionResults.get("GBP")));
    }


    private Map<String, Object> createMockResponse(String currency, BigDecimal rate) {
        Map<String, Object> response = new HashMap<>();
        response.put("result", rate);
        response.put("query", Map.of("to", currency));
        return response;
    }
}
