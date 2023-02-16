package com.springboot.boilerplate.auth;
import com.springboot.boilerplate.auth.payload.request.*;
import com.springboot.boilerplate.auth.payload.response.AuthResponse;
import com.springboot.boilerplate.auth.payload.response.MessageStatusResponse;
import com.springboot.boilerplate.config.JwtService;
import com.springboot.boilerplate.exception.*;
import com.springboot.boilerplate.mailer.MailService;
import com.springboot.boilerplate.usuario.Usuario;
import com.springboot.boilerplate.usuario.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class AuthService {


    @Autowired
    UsuarioService usuarioService;

    @Autowired
    MailService mailService;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    JwtService jwtService;

    @Autowired
    AuthenticationManager authenticationManager;

    private final String RESET_TOKEN_TYPE = "resetToken";

    @Autowired AuthenticationFacade authenticationFacade;

    /**
     * Serviço par registro de usuário
     * @param request parâmetros para o cadastro
     * @return AuthResponse com parâmetros de resposta
     */
    public AuthResponse register(RegisterRequest request) {

        usuarioService.findByEmail(request.getEmail());
        if(usuarioService.usuarioExists(request.getEmail()))
            throw new UsuarioAlreadyExistsException("Usuário já cadastrado");

        if(!request.getPassword().equals(request.getPassword_()))
            throw new PasswordsDontMatchException("As senhas informadas precisam ser iguais");

        Usuario newUser = usuarioService.createNewUsuario(
                request.getEmail(),
                request.getName(),
                passwordEncoder.encode(request.getPassword()));

        String verificationToken = jwtService.generateEmailVerificationToken(newUser);
        mailService.sendEmailVerificationEmail(newUser,verificationToken);

        String jwtToken = jwtService.generateToken(newUser);
        return new AuthResponse(jwtToken);
    }

    /**
     * Serviço para autenticação de usuário com Email e Senha
     * @param request Parâmetros da requisição para login
     * @return
     */
    public AuthResponse authenticate(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );
        Usuario usuario = usuarioService.findByEmail(request.getEmail());
        String jwtToken = jwtService.generateToken(usuario);
        return new AuthResponse(jwtToken);
    }

    /**
     * Serviço para envio de JWT token temporário do RESET_TOKEN_TYPE, por e-mail
     * @param request Parâmetros da requisição para recuperação de senha
     * @return
     */
    public MessageStatusResponse retrievePassword(RetrievePasswordRequest request) {
        Usuario usuario = usuarioService.findByEmail(request.getEmail());
        String jwtToken = jwtService.generateResetPasswordToken(usuario);
        mailService.sendResetPasswordEmail(usuario, jwtToken);
        return new MessageStatusResponse(
                "E-mail de recuperação enviado. Verifique seu e-mail.",
                "sucesso"
        );
    }

    /**
     * Serviço requer usuário autenticado com token regular
     */
    public MessageStatusResponse changePassword(ChangePasswordRequest request) {
        String email = authenticationFacade.getUserEmail();
        Usuario usuario = usuarioService.findByEmail(email);

        if(!passwordEncoder.matches(request.getPassword(), usuario.getPassword()))
            throw new WrongPasswordException("Senha atual incorreta.");

        if(!request.getNewPassword().equals(request.getNewPassword_()))
            throw new PasswordsDontMatchException("As senhas informadas precisam ser iguais");

        usuario.setPassword(passwordEncoder.encode(request.getNewPassword()));
        this.usuarioService.update(usuario);
        return new MessageStatusResponse("Senha alterada com sucesso", "sucesso");
    }

    public MessageStatusResponse emailVerification(String token) {

        String email = jwtService.extractUsername(token);
        Usuario usuario =  usuarioService.findByEmail(email);

        if(usuario == null)
            throw new UsuarioNotFoundException("Usuário não encontrado");

        if(!jwtService.isValidEmailVerificationToken(token, usuario))
            throw new InvalidEmailVerificationToken("Token inválido");

        usuario.setVerified(true);
        this.usuarioService.update(usuario);
        return new MessageStatusResponse("Email verificado com sucesso. Obrigado!", "sucesso");
    }

    /**
     * Serviço para redefinir a senha de usuário SEM a senha atual.
     * Requer usuário autenticado com token temporário do tipo RESET_TOKEN_TYPE não expirado;
     * Se chegou até aqu, é pq passou pelos filtros de segurança, logo usuário é válido e está
     * autenticado. Só precisamos o authorizationHeader (para obter o token) para verificar
     * se o tipo de token é do tipo RESET_TOKEN
     */
    public AuthResponse resetPassword(String authorizationHeader, ResetPasswordRequest request) {
        String email = authenticationFacade.getUserEmail();
        Usuario usuario = usuarioService.findByEmail(email);

        final String jwt = authorizationHeader.substring(7);

        if(!jwtService.isValidResetPasswordToken(jwt, usuario))
            throw new InvalidResetTokenTypeException("Token inválido");

        if(!request.getPassword().equals(request.getPassword_()))
            throw new PasswordsDontMatchException("As senhas informadas precisam ser iguais");

        usuario.setPassword(passwordEncoder.encode(request.getPassword()));
        usuarioService.update(usuario);

        String jwtToken = jwtService.generateToken(usuario);

        return new AuthResponse(jwtToken);
    }


}