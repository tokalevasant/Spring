package com.vht.sbclientforsso.util;

import com.nimbusds.jwt.JWT;
import com.nimbusds.jwt.JWTParser;
import com.vht.sbclientforsso.model.OidcToken;

import java.text.ParseException;
import java.util.Base64;
import java.util.Map;

public class OidcTokenUtils {

    public static String getOidcTokenAsJson(OidcToken oidcToken) {
        StringBuilder oidcTokenJson = new StringBuilder("{ 'accessTokenEncoded': '");
        oidcTokenJson.append(oidcToken.getAccessToken())
                     .append("', 'accessTokenDecoded': ")
                     .append(decodeToken(oidcToken.getAccessToken()))
                     .append(", 'refreshTokenEncoded': '")
                     .append(oidcToken.getRefreshToken())
                     .append("', 'refreshTokenDecoded': ")
                     .append(decodeToken(oidcToken.getRefreshToken()))
                     .append(", 'idTokenEncoded': '")
                     .append(oidcToken.getIdToken())
                     .append("', 'idTokenDecoded': ")
                     .append(decodeToken(oidcToken.getIdToken())).append(" }");

        return oidcTokenJson.toString().replace("'", "\"");
    }

    private static String decodeToken(String token) {
        String[] chunks = token.split("\\.");
        Base64.Decoder decoder = Base64.getUrlDecoder();

        String header = new String(decoder.decode(chunks[0]));
        String payload = new String(decoder.decode(chunks[1]));
        String signature = new String(decoder.decode(chunks[2]));

        StringBuilder sb = new StringBuilder("{ 'header' : ");
        sb.append(header).append(", 'payload': ").append(payload).append(" }");
        return sb.toString();
    }

//    @SneakyThrows
    private static Map<String, Object> getClaims(String token) throws ParseException {
        JWT jwt = JWTParser.parse(token);
        return jwt.getJWTClaimsSet().getClaims();
    }

}
