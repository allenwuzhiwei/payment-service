package com.nusiss.paymentservice.config;

public class CustomException extends RuntimeException {
    public CustomException(String message) {
        super(message);
    }
}