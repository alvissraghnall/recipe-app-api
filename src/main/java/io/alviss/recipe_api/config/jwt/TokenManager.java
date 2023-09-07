package io.alviss.recipe_api.config.jwt;


import static org.apache.commons.lang3.StringUtils.removeStart;

import io.alviss.recipe_api.config.exception.InvalidJwtException;
import io.alviss.recipe_api.user.UserDTO;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.io.Serial;
import java.io.Serializable;
import java.security.KeyPair;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

@Component
public class TokenManager implements Serializable {

    private static final Logger logger = LoggerFactory.getLogger(TokenManager.class);

    private static final KeyPair keypair = Keys.keyPairFor(SignatureAlgorithm.RS384);
    public static final long TOKEN_VALIDITY = 7 * 24 * 60 * 60;
    @Serial
    private static final long serialVersionUID = -4491399601154132982L;
    @Value("${jwt.secret}") private String jwtSecret;
    public String generateJwtToken(UserDTO userPrincipal) {
        // UserDTO userPrincipal = authentication.getPrincipal();
        return Jwts.builder()
                .setSubject(userPrincipal.getUsername())
                .claim("user", userPrincipal.getId().toString())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + TOKEN_VALIDITY * 1000))
                .signWith(keypair.getPrivate())
                .compact();
    }
    public boolean validateJwtToken(String token) {
        String errorMsg;
        try {
            Jwts.parserBuilder().setSigningKey(keypair.getPublic()).build().parseClaimsJws(token);
            return true;
        } catch (SignatureException e) {
            errorMsg = String.format("Invalid JWT Signature: {}", e.getMessage());
            logger.error(errorMsg);
        } catch (MalformedJwtException e) {
            errorMsg = String.format("Invalid JWT Token: {}", e.getMessage());
            logger.error(errorMsg);
        } catch (ExpiredJwtException e) {
            errorMsg = String.format("JWT Token is expired: {}", e.getMessage());
            logger.error(errorMsg);
        } catch (UnsupportedJwtException e) {
            errorMsg = String.format("JWT Token is unsupported: {}", e.getMessage());
            logger.error(errorMsg);
        } catch (IllegalArgumentException e) {
            errorMsg = String.format("JWT Claims string is empty: {}", e.getMessage());
            logger.error(errorMsg);
        } catch (Exception e) {
            errorMsg = String.format("Error: {}", e.getMessage());
            logger.error(errorMsg);
        }

        throw new InvalidJwtException(errorMsg);
    }

    public String getUsernameFromToken(String token) {
        final Claims claims = Jwts.parserBuilder().setSigningKey(keypair.getPublic()).build().parseClaimsJws(token).getBody();
        return claims.getSubject();
    }

    public String getIdFromToken(String token) {
        final Claims claims = Jwts.parserBuilder().setSigningKey(keypair.getPublic()).build().parseClaimsJws(token).getBody();
        return (String) claims.get("user");
    }

}

