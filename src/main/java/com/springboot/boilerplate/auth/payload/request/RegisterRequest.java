package com.springboot.boilerplate.auth.payload.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class RegisterRequest {

    @NotNull(message = "Um nome de usuário precisa ser informado")
    private String name;

    @NotNull(message = "Um email precisa ser informado")
    @Email(message = "Email não possui formato válido")
    private String email;

    @NotNull(message = "Uma senha precisa ser informada")
    @Pattern(regexp= "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$",
            message = "Senha precisa ter no mínimo 8 caracteres, uma letra e um número")
    private String password;

    @NotNull(message = "A senha precisa ser informada duas vezes")
    private String password_;

}