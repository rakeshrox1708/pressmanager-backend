package com.newspaper.System.exception;

import com.newspaper.System.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Object>> handleException(Exception ex) {

        ex.printStackTrace(); // 🔥 VERY IMPORTANT

        return ResponseEntity.badRequest().body(
                new ApiResponse<>(false, ex.getMessage(), null)
        );
    }
}