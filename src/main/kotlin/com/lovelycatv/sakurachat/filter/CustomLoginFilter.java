/*
 * Copyright 2025 lovelycat
 *
 * Use of this source code is governed by the Apache License, Version 2.0,
 * that can be found in the LICENSE file.
 *
 */
package com.lovelycatv.sakurachat.filter;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lovelycatv.sakurachat.request.ApiResponse;
import com.lovelycatv.sakurachat.utils.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;

import java.io.IOException;
import java.util.Collection;

public class CustomLoginFilter extends AbstractAuthenticationProcessingFilter {
    private static final ObjectMapper objectMapper = new ObjectMapper();
 
    public CustomLoginFilter(String defaultFilterProcessesUrl, AuthenticationManager authenticationManager) {
        super(defaultFilterProcessesUrl, authenticationManager);
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        return getAuthenticationManager().authenticate(new UsernamePasswordAuthenticationToken(username, password));
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException {
        Collection<? extends GrantedAuthority> authorities = authResult.getAuthorities();
 
        response.setContentType("application/json;charset=utf-8");
        String userToken = JwtUtil.buildJwtToken("jwtSignKey", authorities, authResult, 7 * 1440 * 60 * 1000L);

        LoginSuccessData data = new LoginSuccessData();
        data.token = userToken;
        data.expiresIn = 15 * 60 * 1000L;

        response.getWriter().write(objectMapper.writeValueAsString(ApiResponse.success(data, "success")));
        response.getWriter().flush();
        response.getWriter().close();
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException {
        response.setContentType("application/json;charset=utf-8");
        response.getWriter().write(
                objectMapper.writeValueAsString(
                        ApiResponse.unauthorized(failed.getMessage() != null
                                ? failed.getMessage()
                                : "username or password incorrect", null
                        )
                )
        );
        response.getWriter().flush();
        response.getWriter().close();
    }

    @JsonAutoDetect(
            fieldVisibility = JsonAutoDetect.Visibility.ANY,
            getterVisibility = JsonAutoDetect.Visibility.NONE,
            isGetterVisibility = JsonAutoDetect.Visibility.NONE
    )
    public static class LoginSuccessData {
        @JsonProperty("token")
        public String token;
        @JsonProperty("expiresIn")
        public long expiresIn;
    }
}