package com.springboot.boilerplate.auth.payload.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class MessageStatusResponse {
    private String message;

    private String status;
}
