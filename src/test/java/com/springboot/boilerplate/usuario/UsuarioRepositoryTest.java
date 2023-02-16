package com.springboot.boilerplate.usuario;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class UsuarioRepositoryTest {

    @Autowired
    UsuarioRepository usuarioRepository;

    @Autowired
    RoleRepository roleRepository;

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
    public void should_create_new_user() {
        createTestUser();
        Usuario userName = usuarioRepository.findByEmail("test@gmail.com");
        assertNotNull(userName, "Usuario não deveria ser null");
        assertEquals(userName.getName(),"Teste User", "Nome do usuário deveria ser compatível");
    }

    @Test
    public void should_not_find_non_registered_user() {
        createTestUser();
        Usuario userName = usuarioRepository.findByEmail("test2@gmail.com");
        assertNull(userName, "Usuario deveria ser null");
    }

    @Test public void should_return_usuarios_size() {
        createTestUser();
        List<Usuario> users = usuarioRepository.findAll();
        assertTrue(users.size() == 1, "users deveria conter um elemento Usuário");
        System.out.println(users);
    }

    @Test public void should_return_correct_authorities() {
        createTestUser();
        Usuario userName = usuarioRepository.findByEmail("test@gmail.com");
        assertTrue(userName.getAuthorities().size() == 1, "Deveria conter apenas uma authority");
        System.out.println(userName);
        assertEquals("ROLE_USER", ((List) userName.getAuthorities()).get(0).toString(), "Role do usuário deveria ser ROLE_USER");
    }

    @Test public void should_return_correct_roles() {
        createTestUser();
        Usuario userName = usuarioRepository.findByEmail("test@gmail.com");

        Set<Role> roles = userName.getRoles();
        Set<Role> result = roles
                .stream()
                .filter(role -> role.getName().equals("ROLE_ADMIN"))
                .collect(Collectors.toSet());

        assertTrue(result.size() == 0, "Usuário não pode ter ROLE_ADMIN");
    }
}