package com.dti.multiwarehouse.auth.service;

import com.dti.multiwarehouse.auth.utils.JwtTokenUtil;
import com.dti.multiwarehouse.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class JwtService {
    private final JwtTokenUtil jwtTokenUtil;

    public String generateToken(User user){
        return jwtTokenUtil.generateToken(user);
    }

    public boolean validateToken(String token){
        return jwtTokenUtil.validateToken(token);
    }
}
