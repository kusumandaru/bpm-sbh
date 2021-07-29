package com.sbh.bpm.exception;

import org.springframework.http.HttpStatus;

public class RestException extends RuntimeException {

    private static final long serialVersionUID = -2338392413116526098L;

    private final HttpStatus status;
    private final String message;

    public RestException(HttpStatus status, String message) {
        super();
        this.status = status;
        this.message = message;
    }

    public RestException(HttpStatus status, String message, Throwable exception) {
        super(exception);
        this.status = status;
        this.message = message;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

}
