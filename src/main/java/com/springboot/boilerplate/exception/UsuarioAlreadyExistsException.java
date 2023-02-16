package com.springboot.boilerplate.exception;

public class UsuarioAlreadyExistsException extends RuntimeException{
    private static final long serialVersionUID = 1L;

    public UsuarioAlreadyExistsException(String msg) {
        super(msg);
    }
}