package com.springboot.boilerplate.securityFilters;

import com.springboot.boilerplate.config.JwtService;
import com.springboot.boilerplate.usuario.Usuario;
import io.jsonwebtoken.ExpiredJwtException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;


import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

public class JwtServiceTest {

    @Autowired
    private MockMvc mvc;

    private JwtService jwtService = new JwtService();

    @BeforeEach
    public void setup(){
        ReflectionTestUtils.setField(
                jwtService,
                "SECRET_KEY",
                "DK4ADKH20GKADKAJF2094HGAKLDAJF294HGALDAJF029U49THTGDKADHF0294");
    }

    private Usuario usuarioFactory(){
        return usuarioFactory("test@user.com");
    }

    private Usuario usuarioFactory(String email) {
        Usuario usuario = new Usuario();
        usuario.setName("Teste user");
        usuario.setEmail(email);
        usuario.setPassword("teste");
        return usuario;
    }

    @Test
    public void should_generate_valid_regular_token() throws NoSuchFieldException {
        Usuario user = usuarioFactory();
        String token = jwtService.generateToken(user);
        assertNotNull(token, "Token gerado não deveria ser null");
        assertTrue(jwtService.isValidAuthenticationToken(token, user), "Token de Autenticação deveria ser válido para o usuário user");
    }

    @Test
    public void should_generate_valid_reset_password_token() throws NoSuchFieldException {
        Usuario user = usuarioFactory();
        String token = jwtService.generateResetPasswordToken(user);
        assertNotNull(token, "Token gerado não deveria ser null");
        assertTrue(jwtService.isValidResetPasswordToken(token, user), "Token deveria ser válido para o usuário user");
    }

    @Test
    public void should_generate_valid_email_verification_token() throws NoSuchFieldException {
        Usuario user = usuarioFactory();
        String token = jwtService.generateEmailVerificationToken(user);
        assertNotNull(token, "Token gerado não deveria ser null");
        assertTrue(jwtService.isValidEmailVerificationToken(token, user), "Token deveria ser válido para o usuário user");
    }

    @Test
    public void isValidResetPasswordToken_should_reject_regular_token() {
        Usuario user = usuarioFactory();
        String tokenRegular = jwtService.generateToken(user);
        assertFalse(jwtService.isValidResetPasswordToken(tokenRegular, user), "Token regular não deve ser considerado RESET TOKEN");
    }

    @Test
    public void isValidEmailVerificationToken_should_reject_regular_token() {
        Usuario user = usuarioFactory();
        String token = jwtService.generateEmailVerificationToken(user);
        String tokenRegular = jwtService.generateToken(user);
        assertFalse(jwtService.isValidResetPasswordToken(tokenRegular, user), "Token regular não deve ser considerado RESET TOKEN");
    }

    @Test
    public void isValidAuthenticationToken_should_reject_reset_password_token() {
        Usuario user = usuarioFactory();
        String token = jwtService.generateResetPasswordToken(user);
        assertFalse(jwtService.isValidAuthenticationToken(token, user), "Token do tipo RESET TOKEN não deve ser considerado AUTHENTICATION TOKEN");
    }

    @Test
    public void isValidAuthenticationToken_should_reject_email_verification_token() {
        Usuario user = usuarioFactory();
        String token = jwtService.generateResetPasswordToken(user);
        assertFalse(jwtService.isValidAuthenticationToken(token, user), "Token do tipo EMAIL VERIFICATION não deve ser considerado AUTHENTICATION TOKEN");
    }

    @Test
    public void should_throw_ExpiredJwtException_when_token_expired(){
        Usuario user = usuarioFactory();

        Usuario otherUser = usuarioFactory("otheruser@test.com");
        String expiredToken = jwtService.generateToken(new HashMap<>(), user, -1000L );

        Exception exception = assertThrows(ExpiredJwtException.class, () -> {
            jwtService.isValidAuthenticationToken(expiredToken, user);
        });

        String expected = "JWT expired";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expected));
    }

    @Test
    public void should_return_false_when_different_user_from_token(){
        Usuario user = usuarioFactory();
        Usuario otherUser = usuarioFactory("otheruser@test.com");

        String expiredUserToken = jwtService.generateToken(user);

        assertFalse(jwtService.isValidAuthenticationToken(expiredUserToken, otherUser), "Usuário diferente do username no token deve retornar falso");
    }
}
