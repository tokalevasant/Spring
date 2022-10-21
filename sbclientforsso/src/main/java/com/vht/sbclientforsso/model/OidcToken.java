package com.vht.sbclientforsso.model;

import lombok.Data;

@Data
public class OidcToken {
    private final String idToken;
    private final String accessToken;
    private final String refreshToken;
}
