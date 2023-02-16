package com.springboot.boilerplate.usuario;

import com.springboot.boilerplate.exception.UsuarioNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UsuarioService implements UserDetailsService {

    @Autowired
    UsuarioRepository usuarioRepository;

    @Autowired
    RoleRepository roleRepository;

    public boolean usuarioExists(String email) {
        return usuarioRepository.findByEmail(email) != null;
    }

    public List<Usuario> findAllUsuarios() {
        return usuarioRepository.findTodos();
    }

    public Usuario createNewUsuario(String email, String nome, String encodedPass) {
        Role userRole = roleRepository.findByName("ROLE_USER");
        Usuario usuario = new Usuario();
        usuario.setName(nome);
        usuario.setEmail(email);
        usuario.setPassword(encodedPass);
        usuario.addRole(userRole);
        usuarioRepository.save(usuario);
        return usuario;
    }

    public Usuario findByEmail(String email) {
        Usuario user = usuarioRepository.findByEmail(email);
        if (user == null)
            throw new UsuarioNotFoundException("Usuário não encontrado");
        return user;
    }

    public void update(Usuario usuario) {
        usuarioRepository.save(usuario);
    }

    @Override
    public Usuario loadUserByUsername(String email) throws UsernameNotFoundException {
        Usuario usuario;
        try {
            usuario = usuarioRepository.findByEmail(email);
        } catch (UsuarioNotFoundException e) {
            throw new UsernameNotFoundException(e.getMessage());
        }
        return usuario;
    }
}
