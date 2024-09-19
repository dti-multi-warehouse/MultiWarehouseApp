package com.dti.multiwarehouse.product.dto.request;

import com.dti.multiwarehouse.category.dao.Category;
import com.dti.multiwarehouse.product.dao.Product;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;

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
    private int price;

    @NotNull
    @Positive
    private Long categoryId;

    public Product toProduct(Category category, List<String> imageUrls) {
        return Product
                .builder()
                .name(name)
                .description(description)
                .price(price)
                .stock(0)
                .category(category)
                .sold(0)
                .imageUrls(new HashSet<>(imageUrls))
                .build();
    }

}
