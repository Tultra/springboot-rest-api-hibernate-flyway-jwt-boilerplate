package com.springboot.boilerplate.exception;

public class PasswordsDontMatchException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public PasswordsDontMatchException(String msg) {
        super(msg);
    }
}
