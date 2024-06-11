package com.example.springmvcmybatis.exception;

import com.example.springmvcmybatis.dto.Error;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException e, HttpHeaders headers, HttpStatusCode status, WebRequest req) {

        String message = e.getBindingResult().getFieldErrors().stream()
                .map(f -> f.getField() + ": " + f.getDefaultMessage())
                .collect(Collectors.joining(", "));

        log.info("Bad request: {}", message);
        return this.handleExceptionInternal(e, new Error(message, HttpStatus.BAD_REQUEST.value()),
                headers, status, req);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Object> handleConstraintViolation(ConstraintViolationException e, WebRequest req) {
        String message = e.getMessage();
        log.info("Bad request: {}", message);
        return this.handleExceptionInternal(e, new Error(message, HttpStatus.BAD_REQUEST.value()),
                new HttpHeaders(), HttpStatus.BAD_REQUEST, req);
    }

    @ExceptionHandler(RestException.class)
    protected ResponseEntity<?> handleRestException(RestException e, WebRequest req) {
        return this.handleExceptionInternal(e, new Error(e.getMessage(), e.getErrorCode()),
                new HttpHeaders(), e.getHttpStatus(), req);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleAllException(Exception e, WebRequest req) {
        log.error("Internal error: {}", e.getMessage());
        e.printStackTrace();
        return this.handleExceptionInternal(e, new Error("Internal error",
                HttpStatus.INTERNAL_SERVER_ERROR.value()), new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR, req);
    }
}
