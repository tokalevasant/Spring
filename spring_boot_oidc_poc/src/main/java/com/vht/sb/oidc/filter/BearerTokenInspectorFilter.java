package com.vht.sb.oidc.filter;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

public class BearerTokenInspectorFilter implements Filter {
    private static final String AUTH_TYPE = "Bearer ";
    private static final String HEADER_AUTH_NAME = "Authorization";

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;

            chain.doFilter(request, response);
    }
}
