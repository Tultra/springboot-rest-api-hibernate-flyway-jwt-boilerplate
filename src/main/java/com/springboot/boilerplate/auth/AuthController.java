package com.springboot.boilerplate.auth;

import com.springboot.boilerplate.auth.payload.request.*;
import com.springboot.boilerplate.auth.payload.response.AuthResponse;
import com.springboot.boilerplate.auth.payload.response.MessageStatusResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    @Autowired
    AuthService service;

    /**
     * Controller para registro de usuário e retorno de um JWT
     */
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request ) {
        return ResponseEntity.ok(service.register(request));
    }

    /**
     * Controller para login de usuário e retorno de um JWT
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request ) {
        return ResponseEntity.ok(service.authenticate(request));
    }

    /**
     * Controller para obter um JWT temporário para fins de redefinição de senha
     */
    @PostMapping("/retrieve-password")
    public ResponseEntity<MessageStatusResponse> retrievePassword(@Valid @RequestBody RetrievePasswordRequest request ) {
        return ResponseEntity.ok(service.retrievePassword(request));
    }

    @GetMapping("/email-verification")
    public ResponseEntity<MessageStatusResponse> emailVerification(@RequestParam("token") String jwt) {
        return ResponseEntity.ok(service.emailVerification(jwt));
    }

    /**
     * Controller para alterar o password quando o usuário não possui acesso à conta,
     * mas obteve um token temporário.
     */
    @PutMapping("/reset-password")
    public ResponseEntity<AuthResponse> resetPassword (@RequestHeader("Authorization") String auth,
                                                       @Valid @RequestBody ResetPasswordRequest request) {
        return ResponseEntity.ok(service.resetPassword(auth, request));
    }

    /**
     * Controller para alterar o password quando o usuário possui acesso à conta, isto é,
     * possui um token de autenticação válido
     */
    @PutMapping("/change-password")
    public ResponseEntity<MessageStatusResponse> changePassword (@Valid @RequestBody ChangePasswordRequest request) {
        return ResponseEntity.ok(service.changePassword(request));
    }

    // TODO Adaptar o /reset-password para usar a mesma lógica do endpoing /auth/email-verification (GET, queryparams)
}