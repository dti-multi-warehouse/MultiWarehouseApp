package com.dti.multiwarehouse.auth.controller;

import com.dti.multiwarehouse.auth.dto.LoginRequestDto;
import com.dti.multiwarehouse.auth.dto.LoginResponseDto;
import com.dti.multiwarehouse.auth.dto.SocialLoginRequest;
import com.dti.multiwarehouse.auth.entity.UserAuth;
import com.dti.multiwarehouse.auth.service.AuthService;
import com.dti.multiwarehouse.auth.service.ClerkService;
import com.dti.multiwarehouse.response.Response;
import com.dti.multiwarehouse.user.entity.User;
import com.dti.multiwarehouse.user.service.UserService;
import jakarta.servlet.http.Cookie;
import lombok.extern.java.Log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

import java.util.Optional;

@RestController
@RequestMapping("/api/v1/auth")
@Validated
@Log
public class AuthController {
    private final AuthService authService;
    private final AuthenticationManager authenticationManager;
    private final ClerkService clerkService;
    private final UserService userService;

    public AuthController(AuthService authService, AuthenticationManager authenticationManager, ClerkService clerkService, UserService userService) {
        this.authService = authService;
        this.authenticationManager = authenticationManager;
        this.clerkService = clerkService;
        this.userService = userService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDto userLogin) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(userLogin.getEmail(), userLogin.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserAuth userDetails = (UserAuth) authentication.getPrincipal();

        LoginResponseDto resp = authService.generateToken(authentication);
        System.out.println(resp.getAccessToken());

        Cookie cookie = new Cookie("Sid", resp.getAccessToken());
        cookie.setMaxAge(3600);
        cookie.setPath("/");
        HttpHeaders headers = new HttpHeaders();
        headers.add("Set-Cookie", cookie.getName() + "=" + cookie.getValue() + "; Path=/; HttpOnly");

        return ResponseEntity.status(HttpStatus.OK).headers(headers).body(resp);
    }

    @PostMapping("/social-login")
    public ResponseEntity<LoginResponseDto> socialLogin(@RequestBody SocialLoginRequest request) {
        boolean isVerified = clerkService.verifyToken(request.getToken());

        if (!isVerified) {
            return ResponseEntity.status(401).body(null);
        }

        Optional<User> userOptional = userService.findByEmail(request.getEmail());
        if (userOptional.isEmpty()) {
            return ResponseEntity.status(404).body(null);
        }

        User user = userOptional.get();
        String token = authService.generateTokenForUser(user);

        LoginResponseDto response = new LoginResponseDto();
        response.setAccessToken(token);
        response.setUserId(String.valueOf(user.getId()));
        response.setEmail(user.getEmail());
        response.setRole(user.getRole());

        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<Response<Object>> logout() {
        authService.logout();

        HttpHeaders headers = new HttpHeaders();
        headers.add("Set-Cookie", "Sid=; Path=/; Max-Age=0; HttpOnly");

        return ResponseEntity.status(HttpStatus.OK).headers(headers).body(Response.success("Logout successfully").getBody());
    }
}
