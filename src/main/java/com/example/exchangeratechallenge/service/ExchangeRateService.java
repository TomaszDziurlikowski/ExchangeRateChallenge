package com.example.exchangeratechallenge.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface ExchangeRateService {

    BigDecimal getExchangeRate(String from, String to);
    Map<String, BigDecimal> getAllExchangeRates(String base);
    BigDecimal convertCurrency(String from,String to, BigDecimal amount);
    Map<String, BigDecimal> convertToMultipleCurrencies(String from, List<String> to, BigDecimal amount);
}
