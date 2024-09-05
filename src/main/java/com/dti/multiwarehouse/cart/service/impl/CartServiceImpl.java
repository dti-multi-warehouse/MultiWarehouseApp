package com.dti.multiwarehouse.cart.service.impl;

import com.dti.multiwarehouse.cart.dao.Cart;
import com.dti.multiwarehouse.cart.dto.AddItemDto;
import com.dti.multiwarehouse.cart.dto.CartItem;
import com.dti.multiwarehouse.cart.dto.GetCartResponseDto;
import com.dti.multiwarehouse.cart.repository.CartRedisRepository;
import com.dti.multiwarehouse.cart.service.CartService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@RequiredArgsConstructor
@Service
public class CartServiceImpl implements CartService {
    private final CartRedisRepository redisRepository;

    @Override
    public void addToCart(String sessionId, AddItemDto requestDto) {
        var cart = redisRepository.findById(sessionId)
                .orElseGet(() -> new Cart(sessionId, new HashMap<>(), 3000));
        cart.addItem(requestDto.getProductId(), requestDto.getQuantity());
        redisRepository.save(cart);
    }

    @Override
    public void removeFromCart(String sessionId, Long productId) {
        var cart = redisRepository.findById(sessionId).orElseThrow(() -> new EntityNotFoundException("Cart not found"));
        cart.removeItem(productId);
        redisRepository.save(cart);
    }

    @Override
    public GetCartResponseDto getCart(String sessionId) {
        var cart = redisRepository.findById(sessionId).orElseGet(() -> new Cart(sessionId, new HashMap<>(), 3000));
        var items = cart.getItems();
        var res = new GetCartResponseDto();
        items.forEach((k,v) -> {
            var cartItem = new CartItem(k, v);
            res.addCartItem(cartItem);
        });
        return res;
    }

    @Override
    public void incrementQuantity(String sessionId, Long productId) {
        var cart = redisRepository.findById(sessionId).orElseThrow(() -> new EntityNotFoundException("Cart not found"));
        cart.incrementQuantity(productId);
        redisRepository.save(cart);

    }

    @Override
    public void decrementQuantity(String sessionId, Long productId) {
        var cart = redisRepository.findById(sessionId).orElseThrow(() -> new EntityNotFoundException("Cart not found"));
        cart.decrementQuantity(productId);
        redisRepository.save(cart);
    }

}
