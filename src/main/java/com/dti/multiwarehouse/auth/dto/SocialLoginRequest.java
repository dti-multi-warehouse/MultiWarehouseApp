package com.dti.multiwarehouse.auth.dto;

import lombok.Data;

@Data
public class SocialLoginRequest {
    private String email;
    private String token;
}
