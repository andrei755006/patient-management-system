package com.pm.authservice.config;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyPair;
import java.security.KeyPairGenerator;

@Configuration
public class KeyGeneratorConfig {

    @Value("${jwt.private-key-path}")
    private String privateKeyPath;

    @Value("${jwt.public-key-path}")
    private String publicKeyPath;

    @PostConstruct
    public void initKeys() throws Exception {
        // Convert string paths from application.yml to Path objects
        Path privPath = Paths.get(privateKeyPath);
        Path pubPath = Paths.get(publicKeyPath);

        // Ensure the directory (/app/certs) exists inside the container
        // This will also appear in your host's shared folder due to Docker volumes
        if (privPath.getParent() != null && !Files.exists(privPath.getParent())) {
            Files.createDirectories(privPath.getParent());
        }

        File privFile = privPath.toFile();
        File pubFile = pubPath.toFile();

        // Generate keys only if they do not already exist on disk
        if (!privFile.exists() || !pubFile.exists()) {
            KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
            generator.initialize(2048);
            KeyPair pair = generator.generateKeyPair();

            // Save Private Key in PKCS#8 format (DER) directly to the shared volume
            try (FileOutputStream fos = new FileOutputStream(privFile)) {
                fos.write(pair.getPrivate().getEncoded());
            }

            // Save Public Key in X.509 format (DER) directly to the shared volume
            try (FileOutputStream fos = new FileOutputStream(pubFile)) {
                fos.write(pair.getPublic().getEncoded());
            }

            System.out.println("RSA keys generated successfully in: " + privPath.getParent());
        } else {
            System.out.println("RSA keys already exist. Skipping generation.");
        }
    }
}