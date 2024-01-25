package com.example.exchangeratechallenge.service;

import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExchangeRateServiceImpl implements ExchangeRateService {


    //TODO ADJUST API PATH AND PARAMS,ADD ACCESS KEY,TESTS,CACHINGDATA
    RestTemplate restTemplate;
    private String API_URI = "";
    private String ACCESS_KEY = "";

    public ExchangeRateServiceImpl(RestTemplate restTemplate){
        this.restTemplate = restTemplate;
    }

    @Override
    public BigDecimal getExchangeRate(String from, String to) {
        String url = UriComponentsBuilder.fromHttpUrl(API_URI)
                .path("/latest")
                .queryParam("base", from)
                .queryParam("symbols", to)
                .toUriString();

        Map<String, Object> response = restTemplate.getForObject(url, Map.class);
        Map<String, BigDecimal> rates = (Map<String, BigDecimal>) response.get("rates");
        return rates.get(to);
    }

    @Override
    public Map<String, BigDecimal> getAllExchangeRates(String base) {
        String url = UriComponentsBuilder.fromHttpUrl(API_URI)
                .path("/latest")
                .queryParam("base", base)
                .toUriString();

        Map<String, Object> response = restTemplate.getForObject(url, Map.class);
        return (Map<String, BigDecimal>) response.get("rates");
    }

    @Override
    public BigDecimal convertCurrency(String from, String to, BigDecimal amount) {
        String url = UriComponentsBuilder.fromHttpUrl(API_URI)
                .path("/convert")
                .queryParam("from", from)
                .queryParam("to", to)
                .queryParam("amount", amount)
                .toUriString();

        Map<String, Object> response = restTemplate.getForObject(url, Map.class);
        Map<String, Object> query = (Map<String, Object>) response.get("query");
        BigDecimal result = new BigDecimal((Double) query.get("result"));
        return result;
    }

    @Override
    public Map<String, BigDecimal> convertToMultipleCurrencies(String from, List<String> currencies, BigDecimal amount) {
        Map<String, BigDecimal> conversionResults = new HashMap<>();
        for (String toCurrency : currencies) {
            BigDecimal convertedAmount = convertCurrency(from, toCurrency, amount);
            conversionResults.put(toCurrency, convertedAmount);
        }
        return conversionResults;
    }
}
