package com.dti.multiwarehouse.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class LoginResponseDto {
    private String accessToken;
    private String userId;
    private String email;
    private String role;
    private Long warehouseId;
    private String warehouseName;
}
