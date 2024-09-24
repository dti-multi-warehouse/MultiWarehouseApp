package com.dti.multiwarehouse.product.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
@Builder
public class ProductDetailsResponseDto {
    private Long id;
    private String name;
    private String description;
    private int price;
    private int stock;
    private String category;
    private List<String> imageUrls;
}
