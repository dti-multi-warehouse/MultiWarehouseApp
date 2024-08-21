package com.dti.multiwarehouse.user.controller;

import com.dti.multiwarehouse.exception.BadRequestException;
import com.dti.multiwarehouse.exception.ResourceNotFoundException;
import com.dti.multiwarehouse.response.Response;
import com.dti.multiwarehouse.user.dto.UserConfirmationRequest;
import com.dti.multiwarehouse.user.dto.UserRegistrationRequest;
import com.dti.multiwarehouse.user.entity.User;
import com.dti.multiwarehouse.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JavaMailSender mailSender;

    @PostMapping("/register")
    public ResponseEntity<Response> register(@RequestBody UserRegistrationRequest request) {
        Optional<User> existingUser = userService.findByEmail(request.getEmail());
        if (existingUser.isPresent()) {
            throw new BadRequestException("Email is already in use");
        }

        User user = new User();
        user.setEmail(request.getEmail());
        user.setVerified(false);
        userService.save(user);

        sendVerificationEmail(request.getEmail());

        return ResponseEntity.ok(new Response(true, "Verification email sent"));
    }

    @PostMapping("/register/confirm")
    public ResponseEntity<Response> confirmRegistration(@RequestBody UserConfirmationRequest request) {
        System.out.println("Received confirmation request for email: " + request.getEmail());
        System.out.println("Received token: " + request.getToken());

        Optional<User> userOptional = userService.findByEmail(request.getEmail());
        if (userOptional.isEmpty()) {
            System.out.println("User not found for email: " + request.getEmail());
            throw new ResourceNotFoundException("User not found");
        }

        User user = userOptional.get();
        if (user.isVerified()) {
            System.out.println("User already verified: " + request.getEmail());
            throw new BadRequestException("User already verified");
        }

        boolean isTokenValid = validateVerificationToken(request.getToken(), user);
        if (!isTokenValid) {
            System.out.println("Invalid or expired token for email: " + request.getEmail());
            throw new BadRequestException("Invalid or expired token");
        }

        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setVerified(true);
        user.setRole("user");
        userService.save(user);

        System.out.println("Registration successful for email: " + request.getEmail());
        return ResponseEntity.ok(new Response(true, "Registration successful"));
    }

    private void sendVerificationEmail(String email) {
        User user = userService.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        String token = generateVerificationToken(user);
        String encodedToken = URLEncoder.encode(token, StandardCharsets.UTF_8);
        String encodedEmail = URLEncoder.encode(email, StandardCharsets.UTF_8);
        String verificationLink = "http://localhost:3000/email-verification?email=" + encodedEmail+"&token=" + encodedToken ;

        System.out.println("Verification link: " + verificationLink);

        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(email);
        mailMessage.setSubject("Complete Your Registration");
        mailMessage.setText("Click the link to complete your registration: " + verificationLink);
        mailSender.send(mailMessage);
    }

    public String generateVerificationToken(User user) {
        String token = UUID.randomUUID().toString();
        Instant expiryDate = Instant.now().plusSeconds(3600); // 1 hour
        String combined = token + "|" + user.getEmail() + "|" + expiryDate.toString();
        String encodedToken = Base64.getEncoder().encodeToString(combined.getBytes());
        System.out.println("Generated token: " + encodedToken);
        return encodedToken;
    }

    private String padBase64Token(String token) {
        int paddingLength = 4 - (token.length() % 4);
        if (paddingLength < 4) {
            token += "=".repeat(paddingLength);
        }
        return token;
    }

    public boolean validateVerificationToken(String token, User user) {
        try {
            System.out.println("Received token: " + token);
            token = padBase64Token(token);
            System.out.println("Padded token: " + token);
            String decodedToken = new String(Base64.getDecoder().decode(token));
            System.out.println("Decoded token: " + decodedToken);
            String[] parts = decodedToken.split("\\|");
            if (parts.length != 3) {
                System.out.println("Token has an incorrect format. Parts: " + parts.length);
                return false;
            }

            String tokenEmail = parts[1];
            Instant expiryDate = Instant.parse(parts[2]);

            System.out.println("Token email: " + tokenEmail);
            System.out.println("User email: " + user.getEmail());
            System.out.println("Token expiry date: " + expiryDate);
            System.out.println("Current UTC time: " + Instant.now());

            boolean isValid = tokenEmail.equals(user.getEmail()) && Instant.now().isBefore(expiryDate);
            System.out.println("Is token valid? " + isValid);
            return isValid;
        } catch (IllegalArgumentException e) {
            System.out.println("Failed to decode the token: " + e.getMessage());
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
