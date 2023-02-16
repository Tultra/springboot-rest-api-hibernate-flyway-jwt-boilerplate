package com.springboot.boilerplate.mailer;

import com.springboot.boilerplate.usuario.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MailPackageRepository extends JpaRepository<MailPackage, Long> {

    List<MailPackage> findByUsuario(Usuario user);

}
