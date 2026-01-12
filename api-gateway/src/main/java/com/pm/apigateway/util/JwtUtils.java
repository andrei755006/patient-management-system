package com.pm.apigateway.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.nio.file.Files;
import java.nio.file.Paths;


import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;

@Component
public class JwtUtils {

    @Value("${jwt.public-key-path}")
    private String publicKeyPath;

    public Claims validateToken(String token) throws Exception {
        return Jwts.parser()
                .verifyWith(loadPublicKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private PublicKey loadPublicKey() throws Exception {
        // Читаем байты напрямую из файловой системы по пути из конфига
        byte[] keyBytes = Files.readAllBytes(Paths.get(publicKeyPath));
        X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        return kf.generatePublic(spec);
    }
}