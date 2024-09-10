package com.dti.multiwarehouse.auth.service;

import com.dti.multiwarehouse.auth.dto.SocialLoginRequest;
import com.dti.multiwarehouse.auth.dto.LoginResponseDto;
import com.dti.multiwarehouse.user.entity.User;
import org.springframework.security.core.Authentication;

public interface AuthService {
    LoginResponseDto generateToken(Authentication authentication);
    void logout();

    boolean authenticateClerk(SocialLoginRequest request);
    String generateTokenForUser(User user);
}
