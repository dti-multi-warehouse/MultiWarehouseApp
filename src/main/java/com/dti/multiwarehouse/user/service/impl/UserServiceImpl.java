package com.dti.multiwarehouse.user.service.impl;

import com.dti.multiwarehouse.exception.ResourceNotFoundException;
import com.dti.multiwarehouse.user.dto.ClerkRegistrationRequest;
import com.dti.multiwarehouse.user.entity.User;
import com.dti.multiwarehouse.user.repository.UserRepository;
import com.dti.multiwarehouse.user.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Base64;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email.toLowerCase());
    }

    @Override
    public User save(User user) {
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
    public void saveEmail(String email) {
        Optional<User> existingUser = userRepository.findByEmail(email.toLowerCase());
        if (existingUser.isPresent()) {
            throw new ResourceNotFoundException("Email is already registered");
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
}