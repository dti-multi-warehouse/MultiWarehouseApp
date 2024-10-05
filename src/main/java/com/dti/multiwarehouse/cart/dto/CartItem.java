package com.dti.multiwarehouse.cart.dto;

import com.dti.multiwarehouse.product.dto.response.ProductDetailsResponseDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartItem {
    private Long productId;
    private String name;
    private int price;
    private int stock;
    private String category;
    private String imageUrl;
    private Integer quantity;

    public CartItem(ProductDetailsResponseDto dto, int quantity) {
        this.productId = dto.getId();
        this.name = dto.getName();
        this.price = dto.getPrice();
        this.stock = dto.getStock();
        this.category = dto.getCategory();
        this.imageUrl = dto.getImageUrls().getFirst();
        this.quantity = quantity;
    }
}
