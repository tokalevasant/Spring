package com.vht.sbclientforsso.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.web.AuthenticatedPrincipalOAuth2AuthorizedClientRepository;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class HandleOAuth2AuthorizedClientRepository implements OAuth2AuthorizedClientRepository {

    private AuthenticatedPrincipalOAuth2AuthorizedClientRepository clientRepository;

    public HandleOAuth2AuthorizedClientRepository(OAuth2AuthorizedClientService authorizedClientService) {
        this.clientRepository = new AuthenticatedPrincipalOAuth2AuthorizedClientRepository(authorizedClientService);
    }

    @Override
    public void saveAuthorizedClient(OAuth2AuthorizedClient authorizedClient,
                                     Authentication principal,
                                     HttpServletRequest request,
                                     HttpServletResponse response) {
        clientRepository.saveAuthorizedClient(authorizedClient, principal, request, response);

    }

    @Override
    public <T extends OAuth2AuthorizedClient> T loadAuthorizedClient(String clientRegistrationId, Authentication principal, HttpServletRequest request) {
        return clientRepository.loadAuthorizedClient(clientRegistrationId, principal, request);
    }

    @Override
    public void removeAuthorizedClient(String clientRegistrationId, Authentication principal, HttpServletRequest request, HttpServletResponse response) {
        clientRepository.removeAuthorizedClient(clientRegistrationId, principal, request, response);
    }
}
