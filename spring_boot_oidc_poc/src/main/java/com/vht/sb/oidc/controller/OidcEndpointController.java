package com.vht.sb.oidc.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Endpoints to call corresponding endpoints on Keycloak
 */
@RestController
@RequestMapping("/oidc")
public class OidcEndpointController {
    public void logout(){

    }

    @GetMapping("/hello")
    public String hello(){
        return "Hello OIDC";
    }
}
