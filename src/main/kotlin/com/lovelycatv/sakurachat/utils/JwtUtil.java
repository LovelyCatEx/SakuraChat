/*
 * Copyright 2025 lovelycat
 *
 * Use of this source code is governed by the Apache License, Version 2.0,
 * that can be found in the LICENSE file.
 *
 */
package com.lovelycatv.sakurachat.utils;

import com.lovelycatv.sakurachat.entity.UserEntity;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.Date;
import java.util.Objects;

public final class JwtUtil {
    private JwtUtil() {
 
    }

    public static String buildJwtToken(String signKey, Collection<? extends GrantedAuthority> authorities, Authentication authentication, long expiration) {
        StringBuilder authorityStr = new StringBuilder();
        for (GrantedAuthority authority : authorities) {
            authorityStr.append(authority).append(",");
        }
 
        return Jwts.builder()
                .claim("authorities", authorityStr)
                .claim(
                    "userId",
                    Objects.requireNonNull(((UserEntity) authentication.getPrincipal()).getId()).toString()
                )
                .setSubject(authentication.getName())
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(SignatureAlgorithm.HS512, signKey)
                .compact();
    }

    public static Claims parseToken(String signKey, String token) {
        return Jwts.parser()
                .setSigningKey(signKey)
                .parseClaimsJws(token.replace("Bearer ", ""))
                .getBody();
    }
}