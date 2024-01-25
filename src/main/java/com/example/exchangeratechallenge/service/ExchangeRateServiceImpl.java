package com.example.exchangeratechallenge.service;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
@Service
public class ExchangeRateServiceImpl implements ExchangeRateService {


    //TODO ADJUST API PATH AND PARAMS,ADD ACCESS KEY,TESTS,CACHINGDATA
    RestTemplate restTemplate;

    CacheManager cacheManager;

    private String API_URI = "http://api.exchangerate.host";
    private String ACCESS_KEY = "ea91a0e9b648ea55dcfa011b08c93a47";

    public ExchangeRateServiceImpl(RestTemplate restTemplate, CacheManager cacheManager){
        this.restTemplate = restTemplate;
        this.cacheManager = cacheManager;
    }

    @Override
    @Cacheable(value = "exchangeRates", key = "#from + '-' + #to")
    public BigDecimal getExchangeRate(String from, String to) {
        String url = UriComponentsBuilder.fromHttpUrl(API_URI)
                .path("/live")
                .queryParam("access_key", ACCESS_KEY)
                .queryParam("source", from)
                .queryParam("currencies", to)
                .toUriString();

        Map<String, Object> response = restTemplate.getForObject(url, Map.class);
        if(response != null && (Boolean)response.get("success")){
            LinkedHashMap<String, Object> quotes = (LinkedHashMap<String, Object>) response.get("quotes");
            if (quotes != null && !quotes.isEmpty()) {
                Map.Entry<String, Object> entry = quotes.entrySet().iterator().next();
                return new BigDecimal(entry.getValue().toString());
            }
        }
        throw new IllegalArgumentException("Unable to fetch exchange rate");
    }

    @Override
    @Cacheable(value = "allExchangeRates", key = "#base")
    public Map<String, BigDecimal> getAllExchangeRates(String base) {
        String url = UriComponentsBuilder.fromHttpUrl(API_URI)
                .path("/live")
                .queryParam("access_key", ACCESS_KEY)
                .queryParam("source", base)
                .toUriString();

        Map<String, Object> response = restTemplate.getForObject(url, Map.class);
        return (Map<String, BigDecimal>) response.get("quotes");
    }

    @Override
    @Cacheable(value = "currencyConversions", key = "#from + '-' + #to + '-' + #amount")
    public BigDecimal convertCurrency(String from, String to, BigDecimal amount) {
        String url = UriComponentsBuilder.fromHttpUrl(API_URI)
                .path("/convert")
                .queryParam("access_key", ACCESS_KEY)
                .queryParam("from", from)
                .queryParam("to", to)
                .queryParam("amount", amount)
                .toUriString();

        Map<String, Object> response = restTemplate.getForObject(url, Map.class);
        BigDecimal result = BigDecimal.valueOf((Double) response.get("result"));
        return result;
    }


    @Override
    @Cacheable(value = "multiCurrencyConversions", key = "#from + '-' +  #currencies  + '-' + #amount")
    public Map<String, BigDecimal> convertToMultipleCurrencies(String from, List<String> currencies, BigDecimal amount) {
            String url = UriComponentsBuilder.fromHttpUrl(API_URI)
                    .path("/live")
                    .queryParam("access_key", ACCESS_KEY)
                    .queryParam("source", from)
                    .queryParam("currencies", String.join(",",currencies))
                    .toUriString();

            Map<String, BigDecimal> conversionResults = new HashMap<>();
            Map<String, Object> response = restTemplate.getForObject(url, Map.class);
        if (response != null && (Boolean) response.get("success")) {
            LinkedHashMap<String, Object> quotes = (LinkedHashMap<String, Object>) response.get("quotes");
            if (quotes != null) {
                for (String currency : currencies) {
                    String pair = from + currency;
                    if (quotes.containsKey(pair)) {
                        BigDecimal rate = new BigDecimal(quotes.get(pair).toString());
                        BigDecimal convertedValue = rate.multiply(amount);
                        conversionResults.put(currency, convertedValue);
                    }
                }
            }
        }
        return conversionResults;
    }
}
