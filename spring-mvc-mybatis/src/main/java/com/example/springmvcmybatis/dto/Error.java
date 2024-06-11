package com.example.springmvcmybatis.dto;

public record Error(
        String message,
        int errorCode
) {}
