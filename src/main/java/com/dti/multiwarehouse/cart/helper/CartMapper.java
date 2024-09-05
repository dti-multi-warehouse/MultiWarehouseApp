package com.dti.multiwarehouse.cart.helper;

import com.dti.multiwarehouse.cart.dto.CartItem;
import com.dti.multiwarehouse.product.dto.response.ProductDetailsResponseDto;

public class CartMapper {
    public static CartItem toCartItem(ProductDetailsResponseDto dto, int quantity) {
        return CartItem.builder()
                .productId(dto.getId())
                .name(dto.getName())
                .price(dto.getPrice())
                .stock(dto.getStock())
                .category(dto.getCategory())
                .imageUrl(dto.getImageUrls().stream().findFirst().orElse(""))
                .quantity(quantity)
                .build();
    }
}
