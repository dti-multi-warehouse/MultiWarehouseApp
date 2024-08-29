package com.dti.multiwarehouse.user.dto;

import lombok.Data;

@Data
public class UserConfirmationRequest {
    private String email;
    private String password;
    private String token;
}