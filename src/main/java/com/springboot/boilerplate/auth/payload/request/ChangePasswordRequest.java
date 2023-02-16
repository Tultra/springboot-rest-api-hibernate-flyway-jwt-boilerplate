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
public class ChangePasswordRequest {

    @NotNull
    private String password;

    @NotNull
    @Pattern(regexp= "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$",
            message = "Nova senha precisa ter no mínimo 8 caracteres, uma letra e um número")
    private String newPassword;

    @NotNull
    private String newPassword_;
}