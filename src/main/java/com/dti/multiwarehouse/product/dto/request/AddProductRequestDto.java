package com.dti.multiwarehouse.product.dto.request;

import com.dti.multiwarehouse.product.dao.Product;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.HashMap;

@Getter
@Setter
@NoArgsConstructor
public class AddProductRequestDto {

    @NotBlank
    private String name;

    @NotBlank
    private String description;

    @NotNull
    @Positive
    private BigDecimal price;

    @NotNull
    @Positive
    private int stock;

    @NotNull
    @Positive
    private Long categoryId;

    public Product toProduct() {
        return Product
                .builder()
                .name(name)
                .description(description)
                .price(price)
                .stock(stock)
                .categoryId(categoryId)
                .build();
    }
}
