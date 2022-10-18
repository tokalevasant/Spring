package com.vht.sb.oidc.controller;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.LaxRedirectStrategy;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;

/**
 * Endpoints to call corresponding endpoints on Keycloak
 */
@RestController
@RequestMapping("/poc")
public class LogoutController {

    private final String logoutEndPoint = "http://localhost:8080/auth/realms/demo/protocol/openid-connect/logout?post_logout_redirect_uri=http://localhost:9095/poc/postlogout&id_token_hint=";

    @GetMapping("/logout")
    public String logout(@RequestParam(name = "id_token") String idToken) {
        final RestTemplate restTemplate = new RestTemplate();
        return  restTemplate.getForObject(logoutEndPoint + idToken, String.class, new HashMap<>());
    }

    @GetMapping("/postlogout")
    public String postLogout() {
        return "Logged out & redirected";
    }

}
