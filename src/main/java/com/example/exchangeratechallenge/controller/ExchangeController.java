package com.example.exchangeratechallenge.controller;

import com.example.exchangeratechallenge.service.ExchangeRateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/exchangeRates")
public class ExchangeController {
    private final ExchangeRateService exchangeRateService;

    @Autowired
    public ExchangeController(ExchangeRateService exchangeRateService) {
        this.exchangeRateService = exchangeRateService;
    }

    @GetMapping("/rate")
    public ResponseEntity<BigDecimal> getExchangeRate(@RequestParam String from, @RequestParam String to) {
        BigDecimal rate = exchangeRateService.getExchangeRate(from, to);
        return ResponseEntity.ok(rate);
    }

    @GetMapping("/rates")
    public ResponseEntity<Map<String, BigDecimal>> getAllExchangeRates(@RequestParam String base) {
        Map<String, BigDecimal> rates = exchangeRateService.getAllExchangeRates(base);
        return ResponseEntity.ok(rates);
    }

    @GetMapping("/convert")
    public ResponseEntity<BigDecimal> convertCurrency(@RequestParam String from, @RequestParam String to, @RequestParam BigDecimal amount) {
        BigDecimal convertedAmount = exchangeRateService.convertCurrency(from, to, amount);
        return ResponseEntity.ok(convertedAmount);
    }

    @GetMapping("/convertMultiple")
    public ResponseEntity<Map<String, BigDecimal>> convertToMultipleCurrencies(@RequestParam String from, @RequestParam List<String> toCurrencies, @RequestParam BigDecimal amount) {
        Map<String, BigDecimal> conversionResults = exchangeRateService.convertToMultipleCurrencies(from, toCurrencies, amount);
        return ResponseEntity.ok(conversionResults);
    }
}
