package com.springboot.boilerplate.mailer;

import com.springboot.boilerplate.usuario.Usuario;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NonNull;

import java.time.LocalDateTime;

/**
 * Classe que define dados a serem passados ao Mailer para envio de e-mails e persistência
 */
@Data
@Entity
@Table(name="emails_enviados")
public class MailPackage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NonNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="usuario_id")
    private Usuario usuario;

    @NonNull
    @Column(name="email_tipo")
    @Enumerated(EnumType.STRING)
    MailPackage.EmailTipos emailTipo;

    @Column(name="data_envio")
    private LocalDateTime dataEnvio = LocalDateTime.now();

    @Column(name="api_response")
    private String apiResponse;

    @Column(name="api_response_status_code")
    private String apiResponseStatusCode;

    public static enum EmailTipos {
        REGISTRO_USUARIO,
        RECUPERA_SENHA,
        CONFIRMA_EMAIL
    }
}
