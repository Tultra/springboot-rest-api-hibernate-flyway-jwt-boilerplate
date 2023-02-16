package com.springboot.boilerplate.auth.payload.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuthResponse {
    String token;
    boolean success;
    String message;
    String type;

    /**
     * Construtor padrão em casos de sucesso no registro/login
     * @param token
     */
    public AuthResponse(String token) {
        this.success = true;
        this.token = token;
        this.message = "Sucesso";
        this.type = "Bearer";
    }

    /**
     * Construtor adequado para casos de erro no registro/autenticação
     */
    public AuthResponse (boolean success, String token, String message) {
        this.success = success;
        this.token = token;
        this.message = message;
        this.type = "";
    }
}
