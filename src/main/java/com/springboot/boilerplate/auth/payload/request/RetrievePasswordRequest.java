package com.springboot.boilerplate.auth.payload.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.*;

@Getter
@Setter
@RequiredArgsConstructor
public class RetrievePasswordRequest {
    @NotNull
    @Email(message="Email não possui formato válido")
    private String email;
}