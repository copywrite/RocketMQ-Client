package com.copywrite.openservices.ons.api.exception;

public class ONSClientException extends RuntimeException {
    private static final long serialVersionUID = 5755356574640041094L;


    public ONSClientException() {
    }


    public ONSClientException(String message) {
        super(message);
    }


    public ONSClientException(Throwable cause) {
        super(cause);
    }


    public ONSClientException(String message, Throwable cause) {
        super(message, cause);
    }
}
