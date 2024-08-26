package com.dti.multiwarehouse.user.service.impl;

import com.dti.multiwarehouse.exception.ResourceNotFoundException;
import com.dti.multiwarehouse.user.dto.ClerkRegistrationRequest;
import com.dti.multiwarehouse.user.entity.User;
import com.dti.multiwarehouse.user.repository.UserRepository;
import com.dti.multiwarehouse.user.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
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

//    @Override
//    public void registerClerk(ClerkRegistrationRequest request) {
//        User user = new User();
//        user.setEmail(request.getEmail());
//        user.setUsername(request.getUsername());
//
//        userRepository.save(user);
//    }

    @Override
    public void saveEmail(String email) {
        Optional<User> existingUser = userRepository.findByEmail(email.toLowerCase());
        if (existingUser.isPresent()) {
            throw new ResourceNotFoundException("Email is already registered");
        }

        User user = new User();
        user.setEmail(email.toLowerCase());
        user.setVerified(true);
        user.setRole("user");
        userRepository.save(user);
    }
}