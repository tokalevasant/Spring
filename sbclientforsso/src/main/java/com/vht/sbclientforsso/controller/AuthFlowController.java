package com.vht.sbclientforsso.controller;

import com.vht.sbclientforsso.config.OAuth2AuthorizedClientProvider;
import com.vht.sbclientforsso.model.OidcToken;
import com.vht.sbclientforsso.util.OidcTokenUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.LaxRedirectStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Generate URLs that can be used with browse, curl, postman etc
 */
@RestController
@RequestMapping("/poc")
@Slf4j
public class AuthFlowController {

    private RestTemplate restTemplate = new RestTemplate();

    private final String authCodeEndPoint = "http://localhost:8080/auth/realms/demo/protocol/openid-connect/auth";
    private final String tokenCodeEndPoint = "http://localhost:8080/auth/realms/demo/protocol/openid-connect/token";
    private final String authCodeRedirectUri = "http://localhost:9095/poc/authCode";
    private final String tokenRedirectUri = "http://localhost:9095/poc/token";
    private final String clientId = "sbclient";
    private final String clientSecret = "7k5SWkblXYsJIzsH5R6QEORUY0XMCBfh";
    private final String state = "mydummystate#2------------------";

    @Autowired
    private OAuth2AuthorizedClientProvider oauth2AuthorizedClientProvider;

    @GetMapping(path = "/user", produces = MediaType.APPLICATION_JSON_VALUE)
    public String getUserInfo(@AuthenticationPrincipal OidcUser oidcUser) {
        OAuth2AuthorizedClient client = oauth2AuthorizedClientProvider.getClient();
        OidcToken oidcToken = new OidcToken(oidcUser.getIdToken().getTokenValue(),
                client.getAccessToken().getTokenValue(),
                client.getRefreshToken().getTokenValue());
        return OidcTokenUtils.getOidcTokenAsJson(oidcToken);
    }

    @GetMapping("/startauthflow")
    public String testResourceParam(HttpServletResponse response) {
        final RestTemplate restTemplate = new RestTemplate();
        final HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
        final HttpClient httpClient = HttpClientBuilder.create()
                                                       .setRedirectStrategy(new LaxRedirectStrategy())
                                                       .build();
        factory.setHttpClient(httpClient);
        restTemplate.setRequestFactory(factory);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();
        map.add("response_type", "code");
        map.add("client_id", clientId);
        map.add("redirect_uri", authCodeRedirectUri);
        map.add("state", state);
        response.addCookie(getCookie("invokeAuthCodeEndPoint", "Hurray1"));
        map.add("scope", "email profile openid");
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);

         String responseEntity = restTemplate.postForObject(authCodeEndPoint, request, String.class);
        log.info(responseEntity);
        return responseEntity;
    }

    private Cookie getCookie(String name, String value) {
        Cookie cookie = new Cookie(name, value);
        // expires in 7 days
        cookie.setMaxAge(7 * 24 * 60 * 60);

        // optional properties
        //        cookie.setSecure(true);
        //        cookie.setHttpOnly(true);
        cookie.setPath("/");
        return cookie;
    }

    @GetMapping(value = "/authCode", produces = MediaType.APPLICATION_JSON_VALUE)
    public String authCode(HttpServletRequest request, HttpServletResponse response,
                           @RequestParam("state") String state,
                           @RequestParam("session_state") String sessionState,
                           @RequestParam("code") String code) {

        log.info("RequestUri: {}", request.getRequestURL());
        log.info("QueryString: {}", request.getQueryString());
        response.addCookie(getCookie("AuthCodeCookieCallback", "Hurray2"));
        final RestTemplate restTemplate = new RestTemplate();
        final HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
        final HttpClient httpClient = HttpClientBuilder.create()
                                                       .setRedirectStrategy(new LaxRedirectStrategy())
                                                       .build();
        factory.setHttpClient(httpClient);
        restTemplate.setRequestFactory(factory);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("grant_type", "authorization_code");
        map.add("client_id", clientId);
        map.add("client_secret", clientSecret);
        map.add("redirect_uri", authCodeRedirectUri);
        map.add("session_state", sessionState);
        map.add("code", code);
        map.add("state", state);
        //        map.add("myresource", "myresourcevalue");
        //        map.add("scope", "email profile openid myresource:myresourcevalue");

        HttpEntity<MultiValueMap<String, String>> newRequest = new HttpEntity<>(map, headers);

        ResponseEntity<String> responseEntity = restTemplate.postForEntity(tokenCodeEndPoint, newRequest, String.class);
        log.info("Token response: {}", responseEntity.getBody());
        return responseEntity.getBody();
    }

    //    @GetMapping("/token")
    //    public String token(HttpServletRequest request) {
    //
    //        log.info("RequestUri: {}", request.getRequestURL());
    //        log.info("QueryString: {}", request.getQueryString());
    //
    //        return "Hurray";
    //    }
}

