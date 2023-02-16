package com.springboot.boilerplate.securityFilters;

import com.springboot.boilerplate.auth.payload.request.*;
import com.springboot.boilerplate.auth.payload.response.AuthResponse;
import com.springboot.boilerplate.auth.payload.response.MessageStatusResponse;
import com.springboot.boilerplate.config.JwtService;
import com.springboot.boilerplate.mailer.MailService;
import com.springboot.boilerplate.auth.AuthService;
import com.springboot.boilerplate.usuario.UsuarioService;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

/**
 * Classe utilizada para testar os security filters para os diversos endpoints do Codeultra,
 * com usuários anônimos, autenticados e autenticados com as respectivas authorities
 */
@SpringBootTest
public class EndpointsSecurityTest {

    @Autowired
    private WebApplicationContext context;

    private MockMvc mvc;

    @BeforeEach
    public void setup() {
        mvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @MockBean
    private AuthService service;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private MailService mailService;

    @MockBean
    private UsuarioService usuarioService;

    @Test
    @WithAnonymousUser
    public void permite_acesso_para_usuario_anonimo_register() throws Exception {
        String TOKEN = "token numero";
        when(service.register(any(RegisterRequest.class)))
                .thenReturn( new AuthResponse(TOKEN));

        JSONObject body = new JSONObject();
        body.put("email", "test@test.com");
        body.put("name", "Test User");
        body.put("password", "password2");
        body.put("password_", "password2");

        mvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body.toString()))
                .andExpect(content().string(containsString(TOKEN)))
                .andExpect(status().isOk());
    }

    @Test
    @WithAnonymousUser
    public void permite_acesso_para_usuario_anonimo_login() throws Exception {
        String TOKEN = "token numero";
        when(service.authenticate(any(LoginRequest.class)))
                .thenReturn(new AuthResponse(TOKEN));

        JSONObject body = new JSONObject();
        body.put("email", "test@test.com");
        body.put("password", "password2");

        mvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body.toString()))
                .andExpect(content().string(containsString(TOKEN)))
                .andExpect(status().isOk());
    }

    @Test
    @WithAnonymousUser
    public void permite_acesso_para_usuario_anonimo_retrievePassword() throws Exception {
        String MESSAGE = "sucesso ok";
        when(service.retrievePassword(any(RetrievePasswordRequest.class)))
                .thenReturn(new MessageStatusResponse(MESSAGE, "ok"));

        JSONObject body = new JSONObject();
        body.put("email", "test@test.com");

        mvc.perform(post("/auth/retrieve-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body.toString()))
                .andExpect(content().string(containsString(MESSAGE)))
                .andExpect(status().isOk());
    }

    @Test
    @WithAnonymousUser
    public void nega_acesso_para_usuario_anonimo_resetPassword() throws Exception {
        JSONObject body = new JSONObject();
        body.put("password", "teste");
        body.put("password_", "teste");

        mvc.perform(put("/auth/reset-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body.toString()))
                .andExpect(status().is4xxClientError());
    }

    @Test
    @WithMockUser(username = "admin", authorities = { "USER" })
    public void permite_acesso_para_usuario_autenticado_resetPassword() throws Exception {
        when(service.resetPassword(anyString(), any(ResetPasswordRequest.class)))
                .thenReturn(new AuthResponse("TOKEN"));

        JSONObject body = new JSONObject();
        body.put("password", "testeteste2");
        body.put("password_", "testeteste2");

        mvc.perform(put("/auth/reset-password")
                        .header("Authorization", "Token") // requires authorization
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body.toString()))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("TOKEN")));
    }

    @Test
    @WithAnonymousUser
    public void nega_acesso_para_usuario_nao_autenticado_changePassword() throws Exception {

        JSONObject body = new JSONObject();
        body.put("password", "testeteste2");
        body.put("newPassword", "testeteste2");
        body.put("newPassword_", "testeteste2");

        mvc.perform(put("/auth/change-password")
                        .header("Authorization", "Token") // requires authorization
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body.toString()))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "admin", authorities = { "USER" })
    public void permite_acesso_para_usuario_autenticado_changePassword() throws Exception {
        String MESSAGE = "sucesso ok";
        when(service.changePassword(any(ChangePasswordRequest.class)))
                .thenReturn(new MessageStatusResponse(MESSAGE, "ok"));

        JSONObject body = new JSONObject();
        body.put("password", "testeteste2");
        body.put("newPassword", "testeteste2");
        body.put("newPassword_", "testeteste2");

        mvc.perform(put("/auth/change-password")
                        .header("Authorization", "Token") // requires authorization
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body.toString()))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString(MESSAGE)));
    }


}
