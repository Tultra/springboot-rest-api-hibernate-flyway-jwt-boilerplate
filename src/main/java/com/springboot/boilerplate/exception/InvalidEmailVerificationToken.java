package com.springboot.boilerplate.exception;

public class InvalidEmailVerificationToken extends RuntimeException{
    private static final long serialVersionUID = 1L;

    public InvalidEmailVerificationToken(String msg) {
        super(msg);
    }
}