package io.alviss.recipe_api.config.jwt;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
// import org.bouncycastle.openssl.PEMKeyPair;
// import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemReader;
import java.io.FileReader;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

@Configuration
public class JwtKeyConfig {

    @Bean
    public PrivateKey privateKey() throws Exception {
        try (FileReader reader = new FileReader("private-key.pem");
             PemReader pemReader = new PemReader(reader)) {

            PemObject pemObject = pemReader.readPemObject();
            byte[] keyBytes = pemObject.getContent();
            PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
            return KeyFactory.getInstance("RSA").generatePrivate(spec);
        }
    }

    @Bean
    public PublicKey publicKey() throws Exception {
        try (FileReader reader = new FileReader("public-key.pem");
             PemReader pemReader = new PemReader(reader)) {

            PemObject pemObject = pemReader.readPemObject();
            byte[] keyBytes = pemObject.getContent();
            X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
            return KeyFactory.getInstance("RSA").generatePublic(spec);
        }
    }
}
