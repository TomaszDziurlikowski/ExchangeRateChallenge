package com.example.exchangeratechallenge.dto;

import java.math.BigDecimal;

public class ConversionResponse {
    private String from;
    private String to;
    private BigDecimal amount;
    private BigDecimal result;

    public ConversionResponse() {
    }

    // Getters and Setters
    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public BigDecimal getResult() {
        return result;
    }

    public void setResult(BigDecimal result) {
        this.result = result;
    }
}
