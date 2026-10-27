package com.pedidos670.ms_rabbit_admin.exception;

import com.pedidos670.ms_rabbit_admin.dto.ApiResponse;

import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.LinkedHashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(
            MethodArgumentNotValidException.class
    )
    public ResponseEntity<Map<String, Object>> handleValidation(
            MethodArgumentNotValidException ex
    ) {

        Map<String, String> errors =
                new LinkedHashMap<>();

        ex.getBindingResult()
                .getFieldErrors()
                .forEach(
                        error ->
                                errors.put(
                                        error.getField(),
                                        error.getDefaultMessage()
                                )
                );

        Map<String, Object> response =
                new LinkedHashMap<>();

        response.put(
                "message",
                "Los datos enviados no son válidos"
        );

        response.put(
                "errors",
                errors
        );

        return ResponseEntity
                .badRequest()
                .body(response);
    }

    @ExceptionHandler(
            IllegalArgumentException.class
    )
    public ResponseEntity<ApiResponse> handleIllegalArgument(
            IllegalArgumentException ex
    ) {

        return ResponseEntity
                .badRequest()
                .body(
                        new ApiResponse(
                                ex.getMessage()
                        )
                );
    }

    @ExceptionHandler(
            RabbitAdminOperationException.class
    )
    public ResponseEntity<ApiResponse> handleRabbitAdminError(
            RabbitAdminOperationException ex
    ) {

        log.error(
                "RabbitMQ administration error",
                ex
        );

        return ResponseEntity
                .status(HttpStatus.BAD_GATEWAY)
                .body(
                        new ApiResponse(
                                ex.getMessage()
                        )
                );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse> handleUnexpectedError(
            Exception ex
    ) {

        log.error(
                "Unexpected application error",
                ex
        );

        return ResponseEntity
                .status(
                        HttpStatus.INTERNAL_SERVER_ERROR
                )
                .body(
                        new ApiResponse(
                                "Se produjo un error interno"
                        )
                );
    }
}