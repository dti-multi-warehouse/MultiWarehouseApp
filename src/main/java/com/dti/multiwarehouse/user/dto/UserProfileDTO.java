package com.dti.multiwarehouse.user.dto;

import lombok.Data;

@Data
public class UserProfileDTO {
    private Long id;
    private String username;
    private String email;
    private boolean isSocial;
    private boolean isVerified;
    private String avatar;
    private String role;
}
