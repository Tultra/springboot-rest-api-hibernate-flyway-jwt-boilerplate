package com.springboot.boilerplate.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String SECRET_KEY;

    private String AUTHENTICATION_TOKEN_TYPE = "AUTH_TOKEN";
    private String RESET_PASSWORD_TOKEN_TYPE = "PASSWORD_RESET";
    private String EMAIL_VERIFICATION_TOKEN_TYPE = "EMAIL_VERIFICATION";

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }
    /**
     * Gera JWT curto, com duração de 30 minutos, para reset de password
     */
    public String generateResetPasswordToken(UserDetails userDetails) {
        // Gera token do tipo RESET_TOKEN_TYPE de curta duração
        Map<String, Object> claims = new HashMap<String, Object>();
        claims.put("type", RESET_PASSWORD_TOKEN_TYPE);
        return generateToken(claims, userDetails, 1_800_000L); // 30 minutos
    }

    /**
     * Gera JWT de verificao de email com duração de 10 dias
     */
    public String generateEmailVerificationToken(UserDetails userDetails) {
        // Gera token do tipo RESET_TOKEN_TYPE de curta duração
        Map<String, Object> claims = new HashMap<String, Object>();
        claims.put("type", EMAIL_VERIFICATION_TOKEN_TYPE);
        return generateToken(claims, userDetails, 864_000_000L); // 10 dias
    }

    /**
     * Gera JWT para autenticação, com duração de 180 dias
     */
    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<String, Object>();
        claims.put("type", AUTHENTICATION_TOKEN_TYPE);
        return generateToken(
                claims,
                userDetails,
                15_552_000_000L); // 180 dias
    }


    /**
     * Gera JWT com prazo de expiração customizado
     */
    public String generateToken(
            Map<String, Object> extraClaims,
            UserDetails userDetails,
            Long expiration
    ) {
        return Jwts
                .builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration)) // 100 dias
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    private boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        Date expDate = extractExpiration(token);
        return expDate.before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private Claims extractAllClaims(String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }


    public boolean isValidEmailVerificationToken(String jwt, UserDetails user){
        if (jwt != null && isEmailVerificationTokenType(jwt) && isTokenValid(jwt, user))
            return true;

        return false;
    }

    public boolean isValidResetPasswordToken(String jwt, UserDetails user){
        if (jwt != null && isResetPasswordToken(jwt) && isTokenValid(jwt, user))
            return true;

        return false;
    }

    /**
     * Token de autenticação, para ser válido, precisa obedecer ao seguinte:
     *  - não pode ser null
     *  - não pode ter expirado
     *  - não pode ser do tipo RESET_PASSWORD_TOKEN_TYPE
     *  - não pode ser do tipo EMAIL_VERIFICATION_TOKEN_TYPE
     * @param jwt
     * @param user UserDetails user
     * @return
     */
    public boolean isValidAuthenticationToken(String jwt, UserDetails user) {
        if (jwt != null
                && isTokenValid(jwt, user)
                && !isResetPasswordToken(jwt)
                && !isEmailVerificationTokenType(jwt))
            return true;
        return false;
    }

    private boolean tokenTypeMatches(String jwt, String type) {
        Function<Claims, String> resolver = claim -> (String) claim.get("type");
        String tokenType = extractClaim(jwt, resolver);
        if (tokenType == null)
            return false;
        return tokenType.equals(type);
    }

    private boolean isResetPasswordToken(String jwt) {
        return tokenTypeMatches(jwt, RESET_PASSWORD_TOKEN_TYPE);
    }

    public boolean isEmailVerificationTokenType(String jwt) {
        return tokenTypeMatches(jwt, EMAIL_VERIFICATION_TOKEN_TYPE);
    }
}