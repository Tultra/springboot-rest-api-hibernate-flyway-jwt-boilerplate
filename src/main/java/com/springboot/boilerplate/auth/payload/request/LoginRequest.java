package com.springboot.boilerplate.auth.payload.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class LoginRequest {
    @NotNull
    @Email(message = "Email não possui formato válido")
    private String email;
    @NotNull
    private String password;

}