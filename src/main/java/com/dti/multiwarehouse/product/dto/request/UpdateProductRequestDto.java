package com.dti.multiwarehouse.product.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
public class UpdateProductRequestDto {
    private String name;

    private String description;

    @Positive
    private BigDecimal price;

    @Positive
    private Long categoryId;

    @NotEmpty
    private Set<String> deletedImageUrls;
}
