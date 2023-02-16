package com.springboot.boilerplate.mailer;

import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import com.springboot.boilerplate.exception.MailerException;
import com.springboot.boilerplate.usuario.Usuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class MailService {

    @Autowired
    MailPackageRepository mailPackageRepository;

    @Value("${sendgrid.api.key}")
    private String apiKey;

    @Value("${sendgrid.verified.user}")
    private String verifiedUser;

    @Async
    public void sendMail(MailPackage mailPackage, String subject, String content_, String contentType) throws IOException {

        // create request object
        Mail mail = new Mail(
                new Email(verifiedUser), // from
                subject, // subject
                new Email(mailPackage.getUsuario().getEmail()), // to
                new Content(contentType, content_) // content
        );

        SendGrid sg = new SendGrid(apiKey);
        Request request = new Request();

        request.setMethod(Method.POST);
        request.setEndpoint("mail/send");
        request.setBody(mail.build());
        Response response = sg.api(request);

        // save na base de dados
        mailPackage.setApiResponse(response.getBody().toString());
        mailPackage.setApiResponseStatusCode(String.valueOf(response.getStatusCode()));
        mailPackageRepository.save(mailPackage);
    }

    // TODO Criar template email verificacao
    public void sendEmailVerificationEmail(Usuario usuario, String emailVerificationToken) {
        // Cria objeto para persistência
        MailPackage mailPackage = new MailPackage(
                usuario,
                MailPackage.EmailTipos.CONFIRMA_EMAIL
        );

        String subject = "Email de verificação";
        String content = "Verifique sua conta: <a href='http://localhost:8080/auth/reset-password?reset_token="
                + emailVerificationToken
                + "'>Link</a>. \n token value: " + emailVerificationToken;
        String contentType = "text/html";

        try {
            sendMail(
                    mailPackage,
                    subject,
                    content,
                    contentType
            );
        } catch (IOException er) {
            throw new MailerException("Ocorreu um erro");
        }
    }

    // TODO criar template reset email
    public void sendResetPasswordEmail(Usuario usuario, String resetToken) {
        // Cria objeto para persistência
        MailPackage mailPackage = new MailPackage(
                usuario,
                MailPackage.EmailTipos.RECUPERA_SENHA
        );

        String subject = "Teste email";
        String content = "Redefina sua senha: <a href='http://localhost:8080/auth/reset-password?reset_token="
                + resetToken
                + "'>Link</a>. \n token value: " + resetToken;
        String contentType = "text/html";

        try {
            sendMail(
                    mailPackage,
                    subject,
                    content,
                    contentType
            );
        } catch (IOException er) {
            throw new MailerException("Ocorreu um erro");
        }
    }
}