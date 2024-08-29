package com.dti.multiwarehouse.auth.helper;

import lombok.extern.java.Log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.Map;

@Log
public class Claims {
    private static final Logger logger = LoggerFactory.getLogger(Claims.class);

    public static Map<String, Object> getClaimsFromJwt() {
        SecurityContext context = SecurityContextHolder.getContext();
        Authentication authentication = context.getAuthentication();

        logger.debug("Extracting claims from JWT for user: {}", authentication.getName());
        Jwt jwt = (Jwt) authentication.getPrincipal();
        Map<String, Object> claims = jwt.getClaims();
        logger.debug("Extracted claims: {}", claims);

        return claims;
    }
}
