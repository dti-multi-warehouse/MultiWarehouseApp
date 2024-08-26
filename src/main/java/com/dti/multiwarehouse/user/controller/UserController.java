package com.dti.multiwarehouse.user.controller;

import com.dti.multiwarehouse.exception.ResourceNotFoundException;
import com.dti.multiwarehouse.response.Response;
import com.dti.multiwarehouse.user.dto.ClerkRegistrationRequest;
import com.dti.multiwarehouse.user.dto.UserConfirmationRequest;
import com.dti.multiwarehouse.user.dto.UserRegistrationRequest;
import com.dti.multiwarehouse.user.entity.User;
import com.dti.multiwarehouse.user.repository.UserRepository;
import com.dti.multiwarehouse.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JavaMailSender mailSender;
    private final UserRepository userRepository;

    @PostMapping("/register")
    public ResponseEntity<Response> register(@RequestBody UserRegistrationRequest request) {
        Optional<User> existingUser = userService.findByEmail(request.getEmail());

        if (existingUser.isPresent()) {
            User user = existingUser.get();
            if (!user.isVerified()) {
                sendRegularVerificationEmail(request.getEmail());
                return ResponseEntity.ok(new Response(true, "Email is already registered but not verified. Verification email resent."));
            } else {
                throw new ResourceNotFoundException("Email is already in use and verified.");
            }
        }

        User user = new User();
        user.setEmail(request.getEmail());
        user.setVerified(false);
        userService.save(user);

        sendRegularVerificationEmail(request.getEmail());

        return ResponseEntity.ok(new Response(true, "Verification email sent"));
    }

    @PostMapping("/save-email")
    public ResponseEntity<String> saveUserEmail(@RequestBody UserRegistrationRequest request) {
        String email = request.getEmail();

        if (email == null || email.isEmpty()) {
            return ResponseEntity.badRequest().body("Email is required");
        }

        try {
            User user = new User();
            System.out.println("Saving email: " + email);
            user.setEmail(email.toLowerCase());
            user.setVerified(true); // Automatically verify social login users
            user.setRole("user");
            userRepository.save(user);
            return ResponseEntity.ok("Email saved successfully. Verification email sent.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to save email");
        }
    }

    @PostMapping("/register/confirm")
    public ResponseEntity<Response> confirmRegistration(@RequestBody UserConfirmationRequest request) {
        String email = request.getEmail().toLowerCase();

        Optional<User> userOptional = userService.findByEmail(email);
        if (userOptional.isEmpty()) {
            throw new ResourceNotFoundException("User not found");
        }

        User user = userOptional.get();
        if (user.isVerified()) {
            throw new ResourceNotFoundException("User already verified");
        }

        boolean isTokenValid = validateVerificationToken(request.getToken(), user);
        if (!isTokenValid) {
            sendRegularVerificationEmail(request.getEmail());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Response(false, "Token expired. A new verification email has been sent."));
        }

        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setVerified(true);
        user.setRole("user");
        userService.save(user);

        return ResponseEntity.ok(new Response(true, "Registration successful"));
    }

    private void sendRegularVerificationEmail(String email) {
        sendVerificationEmail(email, false);
    }

    private void sendSocialVerificationEmail(String email) {
        sendVerificationEmail(email, true);
    }

    private void sendVerificationEmail(String email, boolean isSocial) {
        User user = userService.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        String token = generateVerificationToken(user);
        String encodedToken = URLEncoder.encode(token, StandardCharsets.UTF_8);
        String encodedEmail = URLEncoder.encode(email, StandardCharsets.UTF_8);

        String verificationLink = "http://localhost:3000/email-verification?email=" + encodedEmail + "&token=" + encodedToken;
        if (isSocial) {
            verificationLink += "&social=true";
        }

        System.out.println("verification link: "+verificationLink);

        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(email);
        mailMessage.setSubject("Complete Your Registration");
        mailMessage.setText("Click the link to complete your registration: " + verificationLink);
        mailSender.send(mailMessage);
    }

    private String generateVerificationToken(User user) {
        String token = UUID.randomUUID().toString();
        Instant expiryDate = Instant.now().plusSeconds(3600);
        String combined = token + "|" + user.getEmail() + "|" + expiryDate.toString();
        return Base64.getEncoder().encodeToString(combined.getBytes());
    }

    private boolean validateVerificationToken(String token, User user) {
        try {
            String decodedToken = new String(Base64.getDecoder().decode(token));
            String[] parts = decodedToken.split("\\|");
            if (parts.length != 3) return false;

            String tokenEmail = parts[1];
            Instant expiryDate = Instant.parse(parts[2]);

            return tokenEmail.equals(user.getEmail()) && Instant.now().isBefore(expiryDate);
        } catch (Exception e) {
            return false;
        }
    }
}
