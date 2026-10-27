package com.pedidos670.ms_rabbit_admin.exception;

public class RabbitAdminOperationException extends RuntimeException {

    public RabbitAdminOperationException(String message) {
        super(message);
    }

    public RabbitAdminOperationException(
            String message,
            Throwable cause
    ) {
        super(message, cause);
    }
}