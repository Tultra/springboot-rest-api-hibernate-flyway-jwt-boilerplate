package com.springboot.boilerplate.auth;

import com.springboot.boilerplate.auth.payload.request.*;
import com.springboot.boilerplate.auth.payload.response.AuthResponse;
import com.springboot.boilerplate.auth.payload.response.MessageStatusResponse;
import com.springboot.boilerplate.config.JwtService;
import com.springboot.boilerplate.exception.*;
import com.springboot.boilerplate.usuario.UsuarioService;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthService service;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private UsuarioService usuarioService;

    @Test
    public void register_should_exception_quando_usuario_existe() throws Exception  {
        String ERROR_MESSAGE = "Usuario ja existe";
        when(service.register(any(RegisterRequest.class)))
                .thenThrow( new UsuarioAlreadyExistsException(ERROR_MESSAGE));

        JSONObject body = new JSONObject();
        body.put("email", "test@test.com");
        body.put("name", "Test User");
        body.put("password", "password2");
        body.put("password_", "password2");

        this.mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body.toString()))
                .andExpect(status().is4xxClientError())
                .andExpect(content().string(containsString(ERROR_MESSAGE)));
    }


    @Test
    public void register_should_exception_quando_senhas_nao_iguais() throws Exception  {
        String ERROR_MESSAGE = "Senhas nao batem";

        when(service.register(any(RegisterRequest.class)))
                .thenThrow( new PasswordsDontMatchException(ERROR_MESSAGE));

        JSONObject body = new JSONObject();
        body.put("email", "test@test.com");
        body.put("name", "Test User");
        body.put("password", "password2");
        body.put("password_", "password3");

        this.mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body.toString()))
                .andExpect(status().is4xxClientError())
                .andExpect(content().string(containsString(ERROR_MESSAGE)));
    }

    @Test
    public void register_should_accept_registro_quanto_request_correto() throws Exception {

        JSONObject body = new JSONObject();
        body.put("email", "test@test.com");
        body.put("name", "Test User");
        body.put("password", "password2");
        body.put("password_", "password2");

        this.mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body.toString()))
                .andExpect(status().isOk());
    }

    @Test
    public void register_should_reject_request_name_null() throws Exception {

        JSONObject body = new JSONObject();
        body.put("email", "test@test.com");
        body.put("password", "password2");
        body.put("password_", "password2");

        this.mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body.toString()))
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void register_should_reject_request_quando_senha_fraca() throws Exception {

        JSONObject body = new JSONObject();
        body.put("email", "test@test.com");
        body.put("name", "Test User");
        body.put("password", "passwo");
        body.put("password_", "passwo");

        this.mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body.toString()))
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void register_should_reject_quando_request_email_nao_tem_formato_valido() throws Exception {

        JSONObject body = new JSONObject();
        body.put("email", "testtest.com");
        body.put("name", "Test User");
        body.put("password", "password2");
        body.put("password_", "password2");

        this.mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body.toString()))
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void register_should_reject_quanto_request_senha_duplicada_nao_informada() throws Exception {

        JSONObject body = new JSONObject();
        body.put("email", "test@test.com");
        body.put("name", "Test User");
        body.put("password", "password");

        this.mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body.toString()))
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void login_should_reject_quando_email_invalido() throws Exception {

        JSONObject body = new JSONObject();
        body.put("password", "password");

        this.mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body.toString()))
                .andExpect(status().is4xxClientError());

        JSONObject body2 = new JSONObject();
        body2.put("email", "teste.com");
        body2.put("password", "password");

        this.mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body2.toString()))
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void login_should_reject_quando_password_nao_informado() throws Exception {

        JSONObject body = new JSONObject();
        body.put("email", "test@user.com");

        this.mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body.toString()))
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void login_should_accept_quanto_request_valido() throws Exception {
        String SUCCESS_MESSAGE = "Sucesso ok";

        when(service.authenticate(any(LoginRequest.class)))
                .thenReturn( new AuthResponse(SUCCESS_MESSAGE));

        JSONObject body = new JSONObject();
        body.put("email", "test@user.com");
        body.put("password", "password");

        this.mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body.toString()))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString(SUCCESS_MESSAGE)));
    }

    @Test
    public void retrievePassword_should_accept_quanto_request_valido() throws Exception {
        String SUCCESS_MESSAGE = "Sucesso ok";

        when(service.retrievePassword(any(RetrievePasswordRequest.class)))
                .thenReturn( new MessageStatusResponse(SUCCESS_MESSAGE, "ok"));

        JSONObject body = new JSONObject();
        body.put("email", "test@user.com");

        this.mockMvc.perform(post("/auth/retrieve-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body.toString()))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString(SUCCESS_MESSAGE)));
    }

    @Test
    public void retrievePassword_should_reject_quanto_request_invalido() throws Exception {

        this.mockMvc.perform(post("/auth/retrieve-password")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());

        JSONObject body = new JSONObject();
        body.put("email", "teste.com");

        this.mockMvc.perform(post("/auth/retrieve-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body.toString()))
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void resetPassword_should_accept_request_valido() throws Exception {
        when(service.resetPassword(anyString(), any(ResetPasswordRequest.class)))
                .thenReturn( new AuthResponse("TOKEN"));

        JSONObject body = new JSONObject();
        body.put("password", "password2");
        body.put("password_", "password2");

        this.mockMvc.perform(put("/auth/reset-password")
                        .header("Authorization", "TESTE")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body.toString()))
                .andExpect(status().isOk());
    }

    @Test
    public void resetPassword_should_reject_quando_token_invalido() throws Exception {
        String MESSAGE = "Token invalido";
        when(service.resetPassword(anyString(), any(ResetPasswordRequest.class)))
                .thenThrow( new InvalidResetTokenTypeException(MESSAGE));

        JSONObject body = new JSONObject();
        body.put("password", "password2");
        body.put("password_", "password2");

        this.mockMvc.perform(put("/auth/reset-password")
                        .header("Authorization", "TESTE")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body.toString()))
                .andExpect(status().is4xxClientError())
                .andExpect(content().string(containsString(MESSAGE)));
    }

    @Test
    public void resetPassword_should_reject_quando_senhas_nao_conferem() throws Exception {
        String MESSAGE = "Token invalido";
        when(service.resetPassword(anyString(), any(ResetPasswordRequest.class)))
                .thenThrow( new PasswordsDontMatchException(MESSAGE));

        JSONObject body = new JSONObject();
        body.put("password", "password2");
        body.put("password_", "password2");

        this.mockMvc.perform(put("/auth/reset-password")
                        .header("Authorization", "TESTE")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body.toString()))
                .andExpect(status().is4xxClientError())
                .andExpect(content().string(containsString(MESSAGE)));
    }

    @Test
    public void resetPassword_should_reject_quando_sem_authorization_header() throws Exception {
        JSONObject body = new JSONObject();
        body.put("password", "password2");
        body.put("password_", "password2");

        this.mockMvc.perform(put("/auth/reset-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body.toString()))
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void resetPassword_should_reject_quando_passwords_invalidos() throws Exception {
        JSONObject body = new JSONObject();
        body.put("password", "password");
        body.put("password_", "password2");

        this.mockMvc.perform(put("/auth/reset-password")
                        .header("Authorization", "TESTE")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body.toString()))
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void changePassword_should_accept_quando_passwords_validos() throws Exception {
        JSONObject body = new JSONObject();
        body.put("password", "password");
        body.put("newPassword", "password2");
        body.put("newPassword_", "password2");

        this.mockMvc.perform(put("/auth/change-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body.toString()))
                .andExpect(status().isOk());
    }

    @Test
    public void changePassword_should_reject_quando_passwords_novos_invalidos() throws Exception {
        JSONObject body = new JSONObject();
        body.put("password", "password");
        body.put("newPassword", "password");
        body.put("newPassword_", "password2");

        this.mockMvc.perform(put("/auth/change-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body.toString()))
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void changePassword_should_reject_quando_passwords_novos_diferentes() throws Exception {
        String MESSAGE = "Senhas nao batem";
        when(service.changePassword(any(ChangePasswordRequest.class)))
                .thenThrow( new PasswordsDontMatchException(MESSAGE));

        JSONObject body = new JSONObject();
        body.put("password", "password");
        body.put("newPassword", "password2");
        body.put("newPassword_", "password");

        this.mockMvc.perform(put("/auth/change-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body.toString()))
                .andExpect(status().is4xxClientError())
                .andExpect(content().string(containsString(MESSAGE)));
    }

    @Test
    public void changePassword_should_reject_quando_senha_atual_incorreta() throws Exception {
        String MESSAGE = "Senha atual incorreta";
        when(service.changePassword(any(ChangePasswordRequest.class)))
                .thenThrow( new WrongPasswordException(MESSAGE));

        JSONObject body = new JSONObject();
        body.put("password", "password");
        body.put("newPassword", "password2");
        body.put("newPassword_", "password2");

        this.mockMvc.perform(put("/auth/change-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body.toString()))
                .andExpect(status().is4xxClientError())
                .andExpect(content().string(containsString(MESSAGE)));
    }

    @Test
    public void emailVerification_should_reject_token_invalido() throws Exception {
        String MESSAGE = "Reset token invalido";
        when(service.emailVerification(anyString()))
                .thenThrow( new InvalidResetTokenTypeException(MESSAGE));

        this.mockMvc.perform(get("/auth/email-verification?token=AnyString"))
                .andExpect(status().is4xxClientError())
                .andExpect(content().string(containsString(MESSAGE)));
    }

    @Test
    public void emailVerification_should_reject_usuario_nao_encontrado() throws Exception {
        String MESSAGE = "Reset token invalido";
        when(service.emailVerification(anyString()))
                .thenThrow( new UsuarioNotFoundException(MESSAGE));

        this.mockMvc.perform(get("/auth/email-verification?token=AnyString"))
                .andExpect(status().is4xxClientError())
                .andExpect(content().string(containsString(MESSAGE)));
    }

    @Test
    public void emailVerification_accept_valid_request() throws Exception {
        String MESSAGE = "Reset token invalido";
        when(service.emailVerification(anyString()))
                .thenReturn( new MessageStatusResponse(MESSAGE, "Ok"));

        this.mockMvc.perform(get("/auth/email-verification?token=AnyString"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString(MESSAGE)));
    }
}
