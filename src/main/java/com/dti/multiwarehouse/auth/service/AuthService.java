package com.dti.multiwarehouse.auth.service;

import com.dti.multiwarehouse.auth.dto.LoginResponseDto;
import org.springframework.security.core.Authentication;

public interface AuthService {
    LoginResponseDto generateToken(Authentication authentication);

    void logout();
}
