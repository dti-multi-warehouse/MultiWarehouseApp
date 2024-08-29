package com.dti.multiwarehouse.auth.service;

import com.dti.multiwarehouse.auth.dto.ClerkLoginRequest;
import com.dti.multiwarehouse.auth.dto.LoginResponseDto;
import com.dti.multiwarehouse.auth.helper.Claims;
import com.dti.multiwarehouse.auth.repository.AuthRedisRepository;
import com.dti.multiwarehouse.exception.ResourceNotFoundException;
import com.dti.multiwarehouse.user.entity.User;
import com.dti.multiwarehouse.user.repository.UserRepository;
import com.dti.multiwarehouse.user.service.UserService;
import lombok.extern.java.Log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.stream.Collectors;
@Service
public class AuthServiceImpl implements AuthService {
    private static final Logger logger = LoggerFactory.getLogger(AuthServiceImpl.class);
    private final JwtEncoder jwtEncoder;
    private final AuthRedisRepository authRedisRepository;
    private final UserService userService;
    private final UserRepository userRepository;

    public AuthServiceImpl(JwtEncoder jwtEncoder, AuthRedisRepository authRedisRepository, UserService userService, UserRepository userRepository) {
        this.jwtEncoder = jwtEncoder;
        this.authRedisRepository = authRedisRepository;
        this.userService = userService;
        this.userRepository = userRepository;
    }

    @Override
    public LoginResponseDto generateToken(Authentication authentication) {
        logger.debug("Generating token for user: {}", authentication.getName());
        long userId = userService.findByEmail(authentication.getName()).get().getId();
        Instant now = Instant.now();
        String scope = authentication.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(" "));

        var existingKey = authRedisRepository.getJwtKey(authentication.getName());
        LoginResponseDto response = new LoginResponseDto();
        response.setUserId(Long.toString(userId));
        response.setEmail(authentication.getName());
        response.setRole(scope);

        if (existingKey != null) {
            logger.info("Token already exists for user: {}. Returning existing token.", authentication.getName());
            response.setAccessToken(existingKey);
            return response;
        }

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("self")
                .issuedAt(now)
                .expiresAt(now.plus(10, ChronoUnit.HOURS))
                .subject(authentication.getName())
                .claim("scope", scope)
                .claim("id", userId)
                .build();

        var jwt = jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();

        if (authRedisRepository.isKeyBlacklisted(jwt)) {
            logger.error("JWT Token is blacklisted for user: {}", authentication.getName());
            throw new ResourceNotFoundException("JWT Token has already been blacklisted");
        }

        authRedisRepository.saveJwtKey(authentication.getName(), jwt);
        logger.info("JWT Token generated and saved for user: {}", authentication.getName());
        response.setAccessToken(jwt);
        return response;
    }

    @Override
    public void logout() {
        logger.debug("Logout initiated");
        var claims = Claims.getClaimsFromJwt();
        var email = (String) claims.get("sub");
        String jwt = authRedisRepository.getJwtKey(email);

        if (jwt != null) {
            authRedisRepository.blackListJwt(email, jwt);
            authRedisRepository.deleteJwtKey(email);
            logger.info("User {} logged out and JWT blacklisted", email);
        } else {
            logger.warn("No JWT found for user {} during logout", email);
        }
    }

    public boolean authenticateClerk(ClerkLoginRequest request) {
        Optional<User> user = userRepository.findByEmail(request.getEmail());
        if (user != null) {
            return true;
        }
        return false;
    }
}
