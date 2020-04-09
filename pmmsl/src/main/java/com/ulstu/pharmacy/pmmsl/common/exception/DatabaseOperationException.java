package com.ulstu.pharmacy.pmmsl.common.exception;

public class DatabaseOperationException extends RuntimeException {

    public DatabaseOperationException(Throwable message) {
        super(message);
    }
}