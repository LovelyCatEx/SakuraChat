/*
 * Copyright 2025 lovelycat
 *
 * Use of this source code is governed by the Apache License, Version 2.0,
 * that can be found in the LICENSE file.
 *
 */
package com.lovelycatv.sakurachat.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lovelycatv.sakurachat.request.ApiResponse;
import io.jsonwebtoken.*;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;
import java.util.List;

public class AuthorizationFilter extends GenericFilterBean {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    private final Iterable<RequestMatcher> permitAllMatchers;
 
    public AuthorizationFilter(Iterable<RequestMatcher> permitAllMatchers) {
        this.permitAllMatchers = permitAllMatchers;
    }
 
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
 
        for (RequestMatcher matcher : permitAllMatchers) {
            if (matcher.matches(request)) {
                filterChain.doFilter(request, response);
                return;
            }
        }
 
        String tokenStr = request.getHeader("Authorization");
        try {
            if (tokenStr == null) {
                throw new IllegalStateException("Token invalid");
            }
            Claims claims = Jwts.parser()
                    .setSigningKey("jwtSignKey")
                    .parseClaimsJws(tokenStr.replace("Bearer",""))
                    .getBody();
            String username = claims.getSubject();
            List<? extends GrantedAuthority> authorities = AuthorityUtils.commaSeparatedStringToAuthorityList((String) claims.get("authorities"));
            UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(username, null, authorities);
            SecurityContextHolder.getContext().setAuthentication(token);
            filterChain.doFilter(request, servletResponse);
        } catch (SignatureException | ExpiredJwtException | MalformedJwtException | IllegalStateException e) {
            // JWT signature does not match locally computed signature. JWT validity cannot be asserted and should not be trusted.
 
            // JWT strings must contain exactly 2 period characters. Found: 0
 
            // e.printStackTrace();
            servletResponse.setContentType("application/json;charset=utf-8");
            if (e instanceof ExpiredJwtException) {
                servletResponse.getWriter().write(objectMapper.writeValueAsString(ApiResponse.unauthorized("Token expired", null)));
            } else {
                servletResponse.getWriter().write(objectMapper.writeValueAsString(ApiResponse.unauthorized(e.getMessage(), null)));
            }
            servletResponse.getWriter().flush();
            servletResponse.getWriter().close();
        }
    }
}