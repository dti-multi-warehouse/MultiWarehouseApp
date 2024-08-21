package com.dti.multiwarehouse.user.service.impl;

import com.dti.multiwarehouse.exception.BadRequestException;
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
        return userRepository.findByEmail(email);
    }

    @Override
    public User save(User user) {
        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        } else if (user.isVerified()) {
            throw new BadRequestException("Password cannot be null or empty when verifying user");
        }

        if (user.getRole() == null || user.getRole().isEmpty()) {
            user.setRole("user");
        }

        return userRepository.save(user);
    }
}
