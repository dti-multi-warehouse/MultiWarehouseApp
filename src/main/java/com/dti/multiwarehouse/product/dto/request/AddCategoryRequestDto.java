package com.dti.multiwarehouse.product.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AddCategoryRequestDto {
    @NotBlank
    private String name;
}
