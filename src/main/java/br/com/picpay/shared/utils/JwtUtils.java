package br.com.picpay.shared.utils;

import br.com.picpay.domain.enums.ERole;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

@Component
public class JwtUtils {

    @Value("${jwt-secret-key}")
    public String secretKey;

    public String generateToken(UUID userId, ERole role) {
        Algorithm algorithm = Algorithm.HMAC256(secretKey);
        Instant expiresIn = Instant.now().plus(Duration.ofHours(1));
       return JWT.create().withIssuer("picpay")
                .withSubject(userId.toString())
                .withClaim("role", role.getDescription())
                .withExpiresAt(expiresIn)
                .sign(algorithm);
    }

    public DecodedJWT decodeToken(String token) {
        token = token.replace("Bearer ", "");
        try {
            Algorithm algorithm = Algorithm.HMAC256(secretKey);
            return JWT.require(algorithm).build().verify(token);
        } catch (JWTVerificationException e) {
            throw new RuntimeException("Invalid token");
        }
    }

}
