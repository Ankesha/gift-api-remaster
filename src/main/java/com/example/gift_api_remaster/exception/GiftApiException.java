package com.example.gift_api_remaster.exception;

public class GiftApiException extends RuntimeException {

    public GiftApiException(String message) {
        super(message);
    }

    public GiftApiException(String message, Throwable cause) {
        super(message, cause);
    }
}
