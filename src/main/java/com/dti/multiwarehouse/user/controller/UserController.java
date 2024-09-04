package com.dti.multiwarehouse.user.controller;

import com.dti.multiwarehouse.exceptions.ResourceNotFoundException;
import com.dti.multiwarehouse.response.Response;
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
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JavaMailSender mailSender;
    private final UserRepository userRepository;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody UserRegistrationRequest request) {
        Optional<User> existingUser = userService.findByEmail(request.getEmail());

        if (existingUser.isPresent()) {
            User user = existingUser.get();
            if (!user.isVerified()) {
                sendVerificationEmail(request.getEmail());
                return ResponseEntity.ok(new Response(true, "Email is already registered but not verified. Verification email resent."));
            } else {
                throw new ResourceNotFoundException("Email is already in use and verified.");
            }
        }

        User user = new User();
        user.setEmail(request.getEmail());
        user.setVerified(false);
        userService.save(user);

        sendVerificationEmail(request.getEmail());

        return Response.success("Verification email sent");
    }

    @PostMapping("/save-email")
    public ResponseEntity<?> saveUserEmail(@RequestBody UserRegistrationRequest request) {
        String email = request.getEmail();

        if (email == null || email.isEmpty()) {
            return ResponseEntity.badRequest().body("Email is required");
        }

        try {
            User user = new User();
            System.out.println("Saving email: " + email);
            userService.saveEmail(email);
            return Response.success("Email saved successfully. Verification email sent.");
        } catch (Exception e) {
            return Response.failed(HttpStatus.INTERNAL_SERVER_ERROR.value(),"Failed to save email");
        }
    }

    @PostMapping("/register/confirm")
    public ResponseEntity<?> confirmRegistration(@RequestBody UserConfirmationRequest request) {
        String email = request.getEmail().toLowerCase();

        Optional<User> userOptional = userService.findByEmail(email);
        if (userOptional.isEmpty()) {
            throw new ResourceNotFoundException("User not found");
        }

        User user = userOptional.get();
        if (user.isVerified()) {
            throw new ResourceNotFoundException("User already verified");
        }

        boolean isTokenValid = userService.validateToken(request.getToken(), user);
        if (!isTokenValid) {
            sendVerificationEmail(request.getEmail());
            return Response.failed("Token expired. A new verification email has been sent.");
        }

        user.setPassword(request.getPassword());
        user.setVerified(true);
        user.setSocial(false);
        user.setRole("user");
        userService.save(user);

        return Response.success("Registration successful");
    }


    private void sendVerificationEmail(String email) {
        User user = userService.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        String token = userService.generateToken(user);
        String encodedToken = URLEncoder.encode(token, StandardCharsets.UTF_8);
        String encodedEmail = URLEncoder.encode(email, StandardCharsets.UTF_8);

        String verificationLink = "http://localhost:3000/email-verification?email=" + encodedEmail + "&token=" + encodedToken;

        System.out.println("verification link: "+verificationLink);

        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(email);
        mailMessage.setSubject("Complete Your Registration");
        mailMessage.setText("Click the link to complete your registration: " + verificationLink);
        mailSender.send(mailMessage);
    }

    @PostMapping("/reset-password/request")
    public ResponseEntity<?> requestResetPassword(@RequestBody Map<String,String> request){
        String email = request.get("email");

        Optional<User> userOptional = userService.findByEmail(email);
        if(userOptional.isEmpty()){
            throw  new ResourceNotFoundException("User not found with this email address.");
        }

        User user = userOptional.get();

        if (user.isSocial()) {
            System.out.println("cannot reset password for social user");
            return Response.failed(HttpStatus.FORBIDDEN.value(), "Cannot reset password for a social login account.");
        }

        String token = userService.generateToken(user);
        sendResetPasswordEmail(email, token);

        return Response.success("Password reset email sent");
    }

    @PostMapping("/reset-password/confirm")
    public ResponseEntity<?> resetPassword(@RequestBody Map<String, String> request){
        String email = request.get("email");
        String token = request.get("token");
        String newPassword = request.get("newPassword");

        Optional<User> userOptional = userService.findByEmail(email);
        User user = userOptional.get();

        boolean isValidToken = userService.validateToken(token, user);
        if(!isValidToken){
            sendResetPasswordEmail(email, token);
            return Response.failed("Token expired. A new verification email has been sent");
        }

        if (user.isSocial()) {
            return Response.failed(HttpStatus.FORBIDDEN.value(), "Cannot reset passwor for a social login account");
        }

        userService.resetPassword(email, newPassword);

        return Response.success("Password has been reset successfully");
    }

    private void sendResetPasswordEmail(String email, String token){
        String encodedToken = URLEncoder.encode(token, StandardCharsets.UTF_8);
        String encodedEmail = URLEncoder.encode(email, StandardCharsets.UTF_8);

        String resetLink = "http://localhost:3000/reset-password/confirmation?email="+encodedEmail+"&token="+encodedToken;

        SimpleMailMessage mailMessage = new SimpleMailMessage();

        mailMessage.setTo(email);
        mailMessage.setSubject("Password Reset Request");
        mailMessage.setText("Click the link to reset your password: "+resetLink);
        System.out.println("reset link"+resetLink);
        mailSender.send(mailMessage);
    }
}
