package com.dti.multiwarehouse.auth.service;

import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

@Service
public class ClerkService {

    private final JwtDecoder jwtDecoder;

    public ClerkService(JwtDecoder jwtDecoder) {
        this.jwtDecoder = jwtDecoder;
    }

    public boolean verifyToken(String token) {
        try {
            Jwt decodedToken = jwtDecoder.decode(token);
            System.out.println("Token valid for email: " + decodedToken.getSubject());

            return true;
        } catch (JwtException e) {
            System.err.println("Invalid token: " + e.getMessage());
            return false;
        }
    }
}
