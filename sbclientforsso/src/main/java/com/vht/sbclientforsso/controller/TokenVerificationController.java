package com.vht.sbclientforsso.controller;

import com.auth0.jwk.Jwk;
import com.auth0.jwk.JwkException;
import com.auth0.jwk.JwkProvider;
import com.auth0.jwk.UrlJwkProvider;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.*;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.jose.jwk.JSONWebKeySet;
import org.keycloak.jose.jwk.JWK;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.interfaces.RSAPublicKey;
import java.util.Arrays;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/token")
public class TokenVerificationController {
//    @Value("${spring.security.oauth2.client.provider.keycloak-provider.issuer-uri}")
//    private String issuerUrl;
//    @Value("${spring.security.oauth2.client.provider.keycloak-provider.issuer-uri}/protocol/openid-connect/certs")
//    private String jwksUrl;


    /**
     * Currently works only with RSA keys
     * @param token
     * @return
     */
    @GetMapping("/verify")
    public String isTokenValid(@RequestParam("token") String token) {
        log.debug("Input token: {}", token);

        try {
            DecodedJWT jwt = JWT.decode(token);
            //https://sso.qa.platform.refinitiv.com/auth/realms/ciam/protocol/openid-connect/certs
            JwkProvider provider = new UrlJwkProvider(new URL(jwt.getIssuer()+"/protocol/openid-connect/certs"));

            Jwk jwk = provider.get(jwt.getKeyId());
            String algorithm = jwk.getAlgorithm();
            PublicKey publicKey = jwk.getPublicKey();
            byte[] headerBytes = jwt.getHeader().getBytes(StandardCharsets.UTF_8);
            byte[] payloadBytes = jwt.getPayload().getBytes(StandardCharsets.UTF_8);
            byte[] signatureBytes = jwt.getSignature().getBytes(StandardCharsets.UTF_8);
            boolean b = verifySignatureFor(algorithm, publicKey, headerBytes, payloadBytes, signatureBytes);
            if(!b){
                return "Verification failed, Error logged";
            }
        } catch (JwkException | MalformedURLException | NoSuchAlgorithmException | InvalidKeyException | SignatureException e) {
            log.error("Verification failed", e);
            return "Verification failed: " + e.getMessage();
        }

        return "Token verification successful!";
    }

    private boolean verifySignatureFor(String algorithm, PublicKey publicKey, byte[] headerBytes, byte[] payloadBytes, byte[] signatureBytes) throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        Signature s = Signature.getInstance(algorithm);
        s.initVerify(publicKey);
        s.update(headerBytes);
        s.update((byte) 46);
        s.update(payloadBytes);
        return s.verify(signatureBytes);
    }

}
