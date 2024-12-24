package com.customerpotal_backend.response;

import org.springframework.http.HttpStatus;

import lombok.Data;

@Data
public class ApiResponse<T> {
    private String message;
    private T data;
    private HttpStatus statusCode;
    
    public ApiResponse(String message, T data, HttpStatus notFound) {
        this.message = message;
        this.data = data;
        this.statusCode = notFound;
    }
}
