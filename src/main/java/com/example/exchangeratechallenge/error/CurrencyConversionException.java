package com.example.exchangeratechallenge.error;

public class CurrencyConversionException  extends ExchangeRateServiceException {
    public CurrencyConversionException(String message){
        super(message);
    }
}
