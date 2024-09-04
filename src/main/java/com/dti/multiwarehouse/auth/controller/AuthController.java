package com.dti.multiwarehouse.auth.controller;

import com.dti.multiwarehouse.auth.dto.ClerkLoginRequest;
import com.dti.multiwarehouse.auth.dto.LoginRequestDto;
import com.dti.multiwarehouse.auth.dto.LoginResponseDto;
import com.dti.multiwarehouse.auth.entity.UserAuth;
import com.dti.multiwarehouse.auth.service.AuthService;
import com.dti.multiwarehouse.response.Response;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.java.Log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
@RestController
@RequestMapping("/api/v1")
@Validated
@Log
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
    private final AuthService authService;
    private final AuthenticationManager authenticationManager;

    public AuthController(AuthService authService, AuthenticationManager authenticationManager) {
        this.authService = authService;
        this.authenticationManager = authenticationManager;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDto userLogin) {
        logger.info("User login request received for user: {}", userLogin.getEmail());
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(userLogin.getEmail(), userLogin.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserAuth userDetails = (UserAuth) authentication.getPrincipal();
        logger.info("Token requested for user: {} with roles: {}", userDetails.getUsername(), userDetails.getAuthorities());

        LoginResponseDto resp = authService.generateToken(authentication);

        Cookie cookie = new Cookie("Sid", resp.getAccessToken());
        cookie.setMaxAge(3600);
        cookie.setPath("/");
        HttpHeaders headers = new HttpHeaders();
        headers.add("Set-Cookie", cookie.getName() + "=" + cookie.getValue() + "; Path=/; HttpOnly");

        logger.info("Login successful for user: {}", userLogin.getEmail());
        return ResponseEntity.status(HttpStatus.OK).headers(headers).body(resp);
    }

//    @PostMapping("/loginclerk")
//    public ResponseEntity<String> loginClerk(@RequestBody ClerkLoginRequest request) {
//        boolean isAuthenticated = authService.authenticateClerk(request);
//        if (isAuthenticated) {
//            return ResponseEntity.ok("Clerk login successful");
//        } else {
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Clerk login failed");
//        }
//    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        logger.info("Logout request received");
        authService.logout();

        HttpHeaders headers = new HttpHeaders();
        headers.add("Set-Cookie", "Sid=; Path=/; Max-Age=0; HttpOnly");

        logger.info("Logout successful");
        return ResponseEntity.status(HttpStatus.OK).headers(headers).body("Logout successfully");
    }
}
