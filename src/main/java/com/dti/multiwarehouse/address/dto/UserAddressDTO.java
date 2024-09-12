package com.dti.multiwarehouse.address.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class UserAddressDTO {
    @NotEmpty(message = "Name is required")
    private String name;

    @NotEmpty(message = "Phone number is required")
    private String phoneNumber;

    @NotEmpty(message = "Label is required")
    private String label;

    @NotEmpty(message = "Street is required")
    private String street;

    @NotEmpty(message = "City is required")
    private String city;

    @NotEmpty(message = "Province is required")
    private String province;

    private Double latitude;

    private Double longitude;

    private Boolean isPrimary = false;
}
