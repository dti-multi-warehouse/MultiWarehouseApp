package com.dti.multiwarehouse.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
public class LoginResponseDto {
    private String accessToken;
    private String userId;
    private String email;
    private String role;
}
