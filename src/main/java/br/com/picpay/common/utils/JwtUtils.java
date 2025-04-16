package br.com.picpay.common.utils;

import br.com.picpay.domain.enums.ERole;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

@Component
public class JwtUtils {

    @Value("${jwt-secret-key}")
    public String secretKey;

    private static String staticSecretKey;

    @PostConstruct
    public void init() {
        staticSecretKey = secretKey;
    }

    public static String generateToken(UUID userId, ERole role) {
        Algorithm algorithm = Algorithm.HMAC256(staticSecretKey);
        Instant expiresIn = Instant.now().plus(Duration.ofHours(1));
       return JWT.create().withIssuer("picpay")
                .withSubject(userId.toString())
                .withClaim("role", role.getDescription())
                .withExpiresAt(expiresIn)
                .sign(algorithm);
    }

    public static DecodedJWT decodeToken(String token) {
        token = token.replace("Bearer ", "");
        try {
            Algorithm algorithm = Algorithm.HMAC256(staticSecretKey);
            return JWT.require(algorithm).build().verify(token);
        } catch (JWTVerificationException e) {
            throw new RuntimeException("Invalid token");
        }
    }

}
