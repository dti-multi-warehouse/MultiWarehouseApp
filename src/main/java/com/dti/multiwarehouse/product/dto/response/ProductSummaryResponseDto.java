package com.dti.multiwarehouse.product.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class ProductSummaryResponseDto {
    private Long id;
    private String name;
    private int price;
    private int stock;
    private String category;
    private String thumbnail;
}
