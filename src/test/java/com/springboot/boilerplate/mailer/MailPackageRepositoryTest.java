package com.springboot.boilerplate.mailer;

import com.springboot.boilerplate.usuario.Usuario;
import com.springboot.boilerplate.usuario.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
public class MailPackageRepositoryTest {

    @Autowired
    MailPackageRepository mailPackageRepository;

    @Autowired
    UsuarioRepository usuarioRepository;

    @BeforeEach
    public void setup() {
        Usuario user = new Usuario();
        user.setName("Teste User");
        user.setEmail("test@gmail.com");
        user.setPassword("passwordTest");
        usuarioRepository.save(user);

        MailPackage mail = new MailPackage(user,MailPackage.EmailTipos.RECUPERA_SENHA);
        mail.setApiResponse("Teste Body Response");
        mail.setApiResponseStatusCode("code");
        mailPackageRepository.save(mail);
    }

    @Test
    public void should_create_new_emails_enviados_entry() {
        List<MailPackage> res = mailPackageRepository.findAll();
        assertTrue(res.size() > 0, "res should be present");

        MailPackage mail = res.get(0);
        assertNotNull(mail, "Email não deveria ser null");
        assertEquals(mail.getUsuario().getName(),"Teste User", "Nome do usuário deveria ser compatível com o e-mail");
        assertTrue(mail.getApiResponse().equals("Teste Body Response"));
    }

    @Test
    public void should_find_emails_for_usuario() {
        Usuario user = usuarioRepository.findByEmail("test@gmail.com");
        List<MailPackage> result = mailPackageRepository.findByUsuario(user);
        assertTrue(result.size() == 1, "Deveria ter retornado um e-mail para o usuário user");
        assertTrue(result.get(0).getUsuario().getName() == user.getName());
    }
}
