package com.springboot.boilerplate.exception;

public class InvalidResetTokenTypeException extends RuntimeException{
    private static final long serialVersionUID = 1L;

    public InvalidResetTokenTypeException(String msg) {
        super(msg);
    }
}