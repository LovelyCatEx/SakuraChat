/*
 * Copyright 2025 lovelycat
 *
 * Use of this source code is governed by the Apache License, Version 2.0,
 * that can be found in the LICENSE file.
 *
 */
package com.lovelycatv.sakurachat.utils

import com.lovelycatv.sakurachat.entity.UserEntity
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import org.springframework.security.core.Authentication
import org.springframework.security.core.GrantedAuthority
import java.util.*

object JwtUtil {
    fun buildJwtToken(
        signKey: String?,
        authorities: MutableCollection<out GrantedAuthority?>,
        authentication: Authentication,
        expiration: Long
    ): String? {
        val authorityStr = StringBuilder()
        for (authority in authorities) {
            authorityStr.append(authority).append(",")
        }

        return Jwts.builder()
            .claim("authorities", authorityStr)
            .claim(
                "userId",
                (authentication.principal as UserEntity).id.toString()
            )
            .setSubject(authentication.name)
            .setExpiration(Date(System.currentTimeMillis() + expiration))
            .signWith(SignatureAlgorithm.HS512, signKey)
            .compact()
    }

    fun parseToken(signKey: String?, token: String): Claims? {
        return Jwts.parser()
            .setSigningKey(signKey)
            .parseClaimsJws(token.replace("Bearer ", ""))
            .getBody()
    }
}