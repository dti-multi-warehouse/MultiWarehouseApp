package com.dti.multiwarehouse.product.dto.request;

import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class UpdateProductRequestDto {
    private String name;

    private String description;

    @Positive
    private int price;

    @Positive
    private Long categoryId;

    private List<String> prevImages;
}
