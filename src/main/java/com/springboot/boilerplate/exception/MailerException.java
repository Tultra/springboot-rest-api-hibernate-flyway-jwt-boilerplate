package com.springboot.boilerplate.exception;

public class MailerException extends RuntimeException{
    private static final long serialVersionUID = 1L;

    public MailerException(String msg) {
        super(msg);
    }
}