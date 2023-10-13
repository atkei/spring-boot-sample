package com.example.todoapp.dto;

public record Error(
        String message,
        int errorCode
) {}
