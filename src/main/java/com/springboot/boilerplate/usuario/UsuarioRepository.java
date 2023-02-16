package com.springboot.boilerplate.usuario;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    @Query("SELECT u FROM Usuario u LEFT JOIN FETCH u.roles WHERE u.email = :email")
    Usuario findByEmail(@Param("email") String email);

    @Query("SELECT DISTINCT u FROM Usuario u LEFT JOIN FETCH u.roles r")
    List<Usuario> findTodos();
}
