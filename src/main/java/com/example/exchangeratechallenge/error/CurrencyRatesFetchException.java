package com.example.exchangeratechallenge.error;

public class CurrencyRatesFetchException extends ExchangeRateServiceException{

    public CurrencyRatesFetchException(String message){
        super(message);
    }
}
