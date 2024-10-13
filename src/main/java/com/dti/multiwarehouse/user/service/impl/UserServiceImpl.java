package com.dti.multiwarehouse.user.service.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.dti.multiwarehouse.exceptions.ResourceNotFoundException;
import com.dti.multiwarehouse.user.dto.UserProfileDTO;
import com.dti.multiwarehouse.user.entity.User;
import com.dti.multiwarehouse.user.repository.UserRepository;
import com.dti.multiwarehouse.user.service.UserService;
import jakarta.mail.Multipart;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.Instant;
import java.util.Base64;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final Cloudinary cloudinary;
    private final JavaMailSender mailSender;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, Cloudinary cloudinary, JavaMailSender mailSender) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.cloudinary = cloudinary;
        this.mailSender = mailSender;
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email.toLowerCase());
    }

    @Override
    public User saveUser(User user) {
        user.setEmail(user.getEmail().toLowerCase());

        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            System.out.println("Raw password before encoding: " + user.getPassword());

            user.setPassword(passwordEncoder.encode(user.getPassword()));
            System.out.println("Encoded password: " + user.getPassword());
        } else if (user.isVerified()) {
            throw new ResourceNotFoundException("Password cannot be null or empty when verifying user");
        }

        if (user.getRole() == null || user.getRole().isEmpty()) {
            user.setRole("user");
        }

        return userRepository.save(user);
    }

    public void resetPassword(String email, String newPassword) {
        User user = userRepository.findByEmail(email.toLowerCase())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (passwordEncoder.matches(newPassword, user.getPassword())) {
            throw new IllegalArgumentException("New password must be different from the old password.");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    @Override
    public User updateUserProfile(Long userId, String username, MultipartFile avatar, String password, String email) throws IOException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if(username != null && !username.isEmpty()){
            user.setUsername(username);
        }
        if(avatar != null && !avatar.isEmpty()){
            String avatarUrl = uploadAvatar(avatar);
            System.out.println("Uploaded avatar URL: " + avatarUrl);
            user.setAvatar(avatarUrl);
        }
        if(password != null && !password.isEmpty() && !user.isSocial()){
            user.setPassword(passwordEncoder.encode(password));
        }
        if(email != null && !email.equals(user.getEmail())){
            user.setEmail(email.toLowerCase());
            user.setVerified(false);
            sendVerificationEmail(user);
        }

        return userRepository.save(user);
    }

    private String uploadAvatar(MultipartFile file) throws IOException {
        System.out.println("Uploading file to Cloudinary: " + file.getOriginalFilename());

        Map uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.emptyMap());

        System.out.println("Cloudinary Upload Result: " + uploadResult);

        return uploadResult.get("url").toString();
    }


    @Override
    public void saveEmail(String email) {
        Optional<User> existingUser = userRepository.findByEmail(email.toLowerCase());
        if (existingUser.isPresent()) {
            User user = existingUser.get();
            if (!user.isSocial()) {
                throw new ResourceNotFoundException("Email is already registered as a regular user.");
            }
        }

        User user = new User();
        user.setEmail(email.toLowerCase());
        user.setVerified(true);
        user.setSocial(true);
        user.setRole("user");
        userRepository.save(user);
    }

    @Override
    public String generateToken(User user) {
        String token = UUID.randomUUID().toString();
        Instant expiryDate = Instant.now().plusSeconds(3600);
        String combined = token + "|" + user.getEmail() + "|" + expiryDate.toString();
        return Base64.getEncoder().encodeToString(combined.getBytes());
    }

    @Override
    public boolean validateToken(String token, User user) {
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

    private void sendVerificationEmail(User user) {
        String token = generateToken(user);
        String verificationUrl = "https://alphamarch.shop?token=" + token;
        System.out.println("verification "+verificationUrl);

        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(user.getEmail());
        mailMessage.setSubject("Verify Your Email Address");
        mailMessage.setText("Please click the following link to verify your email address: " + verificationUrl);

        mailSender.send(mailMessage);
    }

    @Override
    public UserProfileDTO getProfile(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        UserProfileDTO userProfileDTO = new UserProfileDTO();
        userProfileDTO.setId(user.getId());
        userProfileDTO.setUsername(user.getUsername());
        userProfileDTO.setEmail(user.getEmail());
        userProfileDTO.setSocial(user.isSocial());
        userProfileDTO.setVerified(user.isVerified());
        userProfileDTO.setAvatar(user.getAvatar());
        userProfileDTO.setRole(user.getRole());

        return userProfileDTO;
    }

    @Override
    public void resendVerificationEmail(String email) {
        User user = userRepository.findByEmail(email.toLowerCase())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (user.isVerified()) {
            throw new IllegalArgumentException("User is already verified.");
        }

        sendVerificationEmail(user);
    }
    @Override
    public void verifyEmail(String token) {
        String decodedToken = new String(Base64.getDecoder().decode(token));
        String[] parts = decodedToken.split("\\|");
        if (parts.length != 3) throw new IllegalArgumentException("Invalid token");

        String tokenEmail = parts[1];
        Instant expiryDate = Instant.parse(parts[2]);

        if (Instant.now().isAfter(expiryDate)) {
            throw new IllegalArgumentException("Token expired");
        }

        User user = userRepository.findByEmail(tokenEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        user.setVerified(true);
        userRepository.save(user);
    }
}