package com.vht.sbclientforsso;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;

import java.util.Map;

@SpringBootApplication
public class SbclientforssoApplication {

	public static void main(String[] args) {
		ConfigurableApplicationContext run = SpringApplication.run(SbclientforssoApplication.class, args);
	}

}
