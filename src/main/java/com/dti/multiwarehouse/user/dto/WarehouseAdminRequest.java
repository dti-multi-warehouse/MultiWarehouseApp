package com.dti.multiwarehouse.user.dto;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class WarehouseAdminRequest {
    private String username;

    @Email(message = "Invalid email format")
    private String email;

    private String password;

    private MultipartFile avatar;
}