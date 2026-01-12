package com.pm.authservice.service;

import com.pm.authservice.model.User;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.time.Instant;
import java.util.Date;
import java.util.Map;

@Service
public class JwtService {

    @Value("${jwt.private-key-path}")
    private String privateKeyPath;

    @Value("${jwt.expiration}")
    private long expiration;

    public String generateToken(User user) throws Exception {
        Instant now = Instant.now();

        // Prepare custom claims (id and roles for Gateway to consume)
        Map<String, Object> claims = Map.of(
                "userId", user.getId(),
                "roles", user.getRoles()
        );

        return Jwts.builder()
                .claims(claims)
                .subject(user.getEmail())
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusMillis(expiration)))
                .signWith(loadPrivateKey(), Jwts.SIG.RS256)
                .compact();
    }

    private PrivateKey loadPrivateKey() throws Exception {
        // Читаем байты напрямую по пути из конфига (например, /app/certs/private_key.der)
        byte[] keyBytes = Files.readAllBytes(Paths.get(privateKeyPath));
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        return kf.generatePrivate(spec);
    }
}