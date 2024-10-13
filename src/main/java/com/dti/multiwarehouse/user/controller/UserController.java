package com.dti.multiwarehouse.user.controller;

import com.dti.multiwarehouse.exceptions.ApplicationException;
import com.dti.multiwarehouse.exceptions.ResourceNotFoundException;
import com.dti.multiwarehouse.response.Response;
import com.dti.multiwarehouse.user.dto.UserConfirmationRequest;
import com.dti.multiwarehouse.user.dto.UserProfileDTO;
import com.dti.multiwarehouse.user.dto.UserRegistrationRequest;
import com.dti.multiwarehouse.user.entity.User;
import com.dti.multiwarehouse.user.repository.UserRepository;
import com.dti.multiwarehouse.user.service.UserService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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

    @PostMapping("/auth/register")
    public ResponseEntity<?> register(@RequestBody UserRegistrationRequest request) {
        Optional<User> existingUser = userService.findByEmail(request.getEmail());

        if (existingUser.isPresent()) {
            User user = existingUser.get();
            if (!user.isVerified()) {
                sendVerificationEmail(request.getEmail());
                return Response.success("Email is already registered but not verified. Verification email resent.");
            } else {
                throw new ApplicationException("Email is already in use and verified.");
            }
        }

        User user = new User();
        user.setEmail(request.getEmail());
        user.setVerified(false);
        userService.saveUser(user);
        sendVerificationEmail(request.getEmail());

        return Response.success("Verification email sent");
    }

    @PostMapping("/auth/register/confirm")
    public ResponseEntity<?> confirmRegistration(@RequestBody UserConfirmationRequest request) {
        String email = request.getEmail().toLowerCase();

        Optional<User> userOptional = userService.findByEmail(email);
        if (userOptional.isEmpty()) {
            throw new ApplicationException("User not found");
        }

        User user = userOptional.get();
        if (user.isVerified()) {
            throw new ApplicationException("User already verified");
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
        userService.saveUser(user);

        return Response.success("Registration successful");
    }

    private void sendVerificationEmail(String email) {
        User user = userService.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        String token = userService.generateToken(user);
        String encodedEmail = URLEncoder.encode(email, StandardCharsets.UTF_8);

        String verificationLink = "\"https://alphamarch.shop/email-verification?email=" + encodedEmail + "&token=" + token;
        System.out.println("verification link: " + verificationLink);

        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(email);
        mailMessage.setSubject("Complete Your Registration");
        mailMessage.setText("Click the link to complete your registration: " + verificationLink);
        mailSender.send(mailMessage);
    }

    @PostMapping("/auth/save-email")
    public ResponseEntity<?> saveUserEmail(@RequestBody UserRegistrationRequest request) {
        String email = request.getEmail();

        if (email == null || email.isEmpty()) {
            return ResponseEntity.badRequest().body("Email is required");
        }

        try {
            System.out.println("Saving email: " + email);
            userService.saveEmail(email);
            return ResponseEntity.ok("Email saved successfully. Verification email sent.");
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Email is already registered as a regular user.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to save email");
        }
    }

    @PostMapping("/auth/check-email")
    public ResponseEntity<Map<String, Boolean>> checkEmail(@RequestBody Map<String, String> request) {
        String email = request.get("email").toLowerCase();
        Optional<User> existingUser = userService.findByEmail(email);
        if (existingUser.isPresent()) {
            return ResponseEntity.ok(Map.of("exists", true));
        }

        return ResponseEntity.ok(Map.of("exists", false));
    }

    @PutMapping("/updateProfile")
    public ResponseEntity<?> updateUserProfile(
            @RequestParam("userId") Long userId,
            @RequestParam(value = "username", required = false) String username,
            @RequestParam(value = "avatar", required = false) MultipartFile avatar,
            @RequestParam(value = "password", required = false) String password,
            @RequestParam(value = "email", required = false) String email) {

        try {
            userService.updateUserProfile(userId, username, avatar, password, email);
            return ResponseEntity.ok("Profile updated. Please verify your new email if it was changed.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to update profile: " + e.getMessage());
        }
    }

    @GetMapping("/profile")
    public ResponseEntity<?> getProfile(@RequestParam("userId") Long userId) {
        try {
            UserProfileDTO userProfile = userService.getProfile(userId);
            return ResponseEntity.ok(userProfile);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to retrieve profile: " + e.getMessage());
        }
    }

    @GetMapping("/auth/verify-email")
    public ResponseEntity<?> verifyEmail(@RequestParam("token") String token, HttpSession session) {
        try {
            userService.verifyEmail(token);
            String redirectUrl = session.getAttribute("user") != null ? "\"https://alphamarch.shop/my-profile" : "\"https://alphamarch.shop";
            return ResponseEntity.ok().header("Location", redirectUrl).build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Verification failed: " + e.getMessage());
        }
    }

    @PostMapping("/auth/resend-verification-email")
    public ResponseEntity<?> resendVerificationEmail(@RequestParam("email") String email) {
        try {
            userService.resendVerificationEmail(email);
            return ResponseEntity.ok("Verification email has been resent.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to resend verification email: " + e.getMessage());
        }
    }

    @PostMapping("/auth/reset-password/request")
    public ResponseEntity<?> requestResetPassword(@RequestBody Map<String,String> request){
        String email = request.get("email");

        Optional<User> userOptional = userService.findByEmail(email);
        if(userOptional.isEmpty()){
            throw new ApplicationException("User not found with this email address.");
        }

        User user = userOptional.get();
        if (user.isSocial()) {
            return Response.failed("Cannot reset password for a social login account.");
        }

        String token = userService.generateToken(user);
        sendResetPasswordEmail(email, token);
        return Response.success("Password reset email sent");
    }

    @PostMapping("/auth/reset-password/confirm")
    public ResponseEntity<?> resetPassword(@RequestBody Map<String, String> request){
        String email = request.get("email");
        String token = request.get("token");
        String newPassword = request.get("newPassword");

        Optional<User> userOptional = userService.findByEmail(email);
        User user = userOptional.get();

        boolean isValidToken = userService.validateToken(token, user);
        if(!isValidToken){
            sendResetPasswordEmail(email, token);
            return Response.failed("Token expired. A new verification email has been sent.");
        }

        if (user.isSocial()) {
            return Response.failed("Cannot reset password for a social login account.");
        }

        userService.resetPassword(email, newPassword);
        return Response.success("Password has been reset successfully");
    }

    private void sendResetPasswordEmail(String email, String token){
//        String encodedToken = URLEncoder.encode(token, StandardCharsets.UTF_8);
        String encodedEmail = URLEncoder.encode(email, StandardCharsets.UTF_8);

        String resetLink = "\"https://alphamarch.shop/reset-password/confirmation?email="+encodedEmail+"&token="+token;
        SimpleMailMessage mailMessage = new SimpleMailMessage();

        mailMessage.setTo(email);
        mailMessage.setSubject("Password Reset Request");
        mailMessage.setText("Click the link to reset your password: "+resetLink);
        System.out.println("reset link"+resetLink);
        mailSender.send(mailMessage);
    }
}
