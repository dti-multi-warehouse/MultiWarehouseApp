package com.dti.multiwarehouse.product.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class ProductSummaryResponseDto {
    private Long id;
    private String name;
    private BigDecimal price;
    private int stock;
    private Long categoryId;
}
