package com.dti.multiwarehouse.user.service;

import com.dti.multiwarehouse.user.dto.ClerkRegistrationRequest;
import com.dti.multiwarehouse.user.dto.UserProfileDTO;
import com.dti.multiwarehouse.user.entity.User;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

public interface UserService {
    Optional<User> findByEmail(String Email);
    User saveUser(User user);
    void saveEmail(String email);
    void resetPassword(String email, String newPassword);
    String generateToken(User user);
    boolean validateToken(String token, User user);
    User updateUserProfile(Long userId, String username, MultipartFile avatar, String password, String email) throws IOException;
    void resendVerificationEmail(String email);
    UserProfileDTO getProfile(Long userId);
    void verifyEmail(String token);
}
