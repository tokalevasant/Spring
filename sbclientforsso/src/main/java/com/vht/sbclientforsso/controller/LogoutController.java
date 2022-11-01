package com.vht.sbclientforsso.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;

/**
 * Endpoints to call corresponding endpoints on Keycloak
 */
@RestController
@Slf4j
public class LogoutController {

    //    private final String logoutEndPoint = "http://localhost:8080/auth/realms/demo/protocol/openid-connect/logout?post_logout_redirect_uri=http://localhost:9095/poc/postlogout&id_token_hint="; //local
    private final String logoutEndPoint = "https://sentinel-rhsso-dev-us.desktop-preprod.qa.aws.private.inf0.net/auth/realms/demo/protocol/openid-connect/logout?state=somestate&post_logout_redirect_uri=https://www.espncricinfo.com/&id_token_hint=";

    @GetMapping("/sso-logout")
    public String logout(@AuthenticationPrincipal OidcUser oidcUser,
                         HttpServletResponse response,
                         HttpServletRequest request) throws IOException, ServletException {
        final RestTemplate restTemplate = new RestTemplate();
        String s = logoutEndPoint + oidcUser.getIdToken().getTokenValue();
        log.debug("logoutendpoint: {}", s);
//        response.sendRedirect("https://sentinel-rhsso-dev-us.desktop-preprod.qa.aws.private.inf0.net/auth/realms/demo/protocol/openid-connect/logout");
        request.logout();
        Cookie[] cookies = request.getCookies();


        Arrays.stream(cookies).forEach(c -> {
//            c.setMaxAge(0);
//            c.setValue("");
//            response.addCookie(c);
            log.debug("Cookie: {}", c);
        });
        Collection<String> headerNames = response.getHeaderNames();
        headerNames.forEach(n -> log.debug("Header name: {}, val: {}", n, response.getHeader(n)));
        response.sendRedirect(s);
//        String result = restTemplate.getForObject(s, String.class, new HashMap<>());
//        log.debug("Response: {}", result);
        return "Done";
    }

    @GetMapping("/postlogout")
    public String postLogout(HttpServletResponse response, HttpServletRequest request) throws ServletException {

        return "Logged out & redirected";
    }

}
