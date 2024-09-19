package com.dti.multiwarehouse.cart.service;

import com.dti.multiwarehouse.cart.dto.AddItemDto;
import com.dti.multiwarehouse.cart.dto.GetCartResponseDto;

public interface CartService {
    void addToCart(String sessionId, AddItemDto requestDto);
    void removeFromCart(String sessionId, Long productId);
    GetCartResponseDto getCart(String sessionId);
    void incrementQuantity(String sessionId, Long productId);
    void decrementQuantity(String sessionId, Long productId);
    void deleteCart(String sessionId);
}
