package com.example.springmvcmybatis.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class RestException extends RuntimeException {
    private final HttpStatus httpStatus;
    private final int errorCode;

    public RestException(HttpStatus httpStatus, String message) {
        super(message);
        this.httpStatus = httpStatus;
        this.errorCode = httpStatus.value();
    }
}
