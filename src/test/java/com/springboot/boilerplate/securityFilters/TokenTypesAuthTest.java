package com.springboot.boilerplate.securityFilters;

import com.springboot.boilerplate.mailer.MailService;
import com.springboot.boilerplate.auth.AuthService;
import com.springboot.boilerplate.config.JwtService;
import com.springboot.boilerplate.config.WebSecurityConfig;
import com.springboot.boilerplate.usuario.*;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.test.autoconfigure.core.AutoConfigureCache;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureDataJpa;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureTestEntityManager;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
//@ExtendWith({org.springframework.test.context.junit.jupiter.SpringExtension.class})
//@OverrideAutoConfiguration(enabled = false)
@Transactional
@AutoConfigureCache
@AutoConfigureDataJpa
@AutoConfigureTestDatabase
@AutoConfigureTestEntityManager
@ImportAutoConfiguration
@Import({WebSecurityConfig.class, JwtService.class, AuthService.class})
public class TokenTypesAuthTest {

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    private WebApplicationContext context;

    private MockMvc mvc;

    @Autowired
    private AuthService service;

    @Autowired
    private JwtService jwtService;

    @MockBean
    private MailService mailService;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    public SecurityFilterChain securityFilterChain;

    @BeforeEach
    public void setup() {
        mvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();

        ReflectionTestUtils.setField(
                jwtService,
                "SECRET_KEY",
                "DK4ADKH20GKADKAJF2094HGAKLDAJF294HGALDAJF029U49THTGDKADHF0294");
    }

    private void createTestUser() {
        Role role = roleRepository.findByName("ROLE_USER");
        System.out.println(role);
        Usuario user = new Usuario();
        user.setName("Teste User");
        user.setEmail("test@gmail.com");
        user.setPassword("passwordTest");
        user.addRole(role);
        usuarioRepository.save(user);
    }

    @Test
    public void permite_acesso_para_usuario_com_token_autenticacao() throws Exception {
        createTestUser();
        String TOKEN = "token numero";
        Usuario user = usuarioRepository.findByEmail("test@gmail.com");
        String token = jwtService.generateToken(user);

        System.out.println(token);

        JSONObject body = new JSONObject();

        mvc.perform(get("/api/list-usuarios")
                        .header("Authorization", "Bearer " + token)
                        .header("Accept", "application/json"))
                .andExpect(content().string(containsString("test@gmail.com")))
                .andExpect(status().isOk());
    }

    @Test
    public void nega_acesso_para_usuario_com_token_verificacao_email() throws Exception {
        createTestUser();
        Usuario user = usuarioRepository.findByEmail("test@gmail.com");
        String token = jwtService.generateEmailVerificationToken(user);

        System.out.println(token);

        JSONObject body = new JSONObject();

        mvc.perform(get("/api/list-usuarios")
                        .header("Authorization", "Bearer " + token)
                        .header("Accept", "application/json"))
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void nega_acesso_para_usuario_com_token_reset_senha() throws Exception {
        createTestUser();
        Usuario user = usuarioRepository.findByEmail("test@gmail.com");
        String token = jwtService.generateResetPasswordToken(user);

        System.out.println(token);

        JSONObject body = new JSONObject();

        mvc.perform(get("/api/list-usuarios")
                        .header("Authorization", "Bearer " + token)
                        .header("Accept", "application/json"))
                .andExpect(status().is4xxClientError());
    }
}
