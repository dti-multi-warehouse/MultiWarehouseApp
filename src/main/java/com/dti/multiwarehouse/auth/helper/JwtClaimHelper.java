package com.dti.multiwarehouse.auth.helper;

import io.jsonwebtoken.Claims;
import org.springframework.stereotype.Component;

@Component
public class JwtClaimHelper {

    public String getRoleFromClaims(Claims claims) {
        return claims.get("role", String.class);
    }

    public String getEmailFromClaims(Claims claims) {
        return claims.getSubject();
    }
}