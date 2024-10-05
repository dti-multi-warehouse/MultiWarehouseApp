package com.dti.multiwarehouse.product.dto.response;

import com.dti.multiwarehouse.product.dao.Product;
import lombok.Getter;

import java.util.List;

@Getter
public class ProductDetailsResponseDto {
    private final Long id;
    private final String name;
    private final String description;
    private final int price;
    private final int stock;
    private final String category;
    private final List<String> imageUrls;

    public ProductDetailsResponseDto(Product product) {
        this.id = product.getId();
        this.name = product.getName();
        this.description = product.getDescription();
        this.price = product.getPrice();
        this.stock = product.getStock();
        this.category = product.getCategory().getName();
        this.imageUrls = product.getImageUrls();
    }
}
