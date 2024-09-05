package com.dti.multiwarehouse.cart.service.impl;

import com.dti.multiwarehouse.cart.dao.Cart;
import com.dti.multiwarehouse.cart.dto.AddItemDto;
import com.dti.multiwarehouse.cart.dto.CartItem;
import com.dti.multiwarehouse.cart.dto.GetCartResponseDto;
import com.dti.multiwarehouse.cart.helper.CartMapper;
import com.dti.multiwarehouse.cart.repository.CartRedisRepository;
import com.dti.multiwarehouse.cart.service.CartService;
import com.dti.multiwarehouse.product.service.ProductService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;

@RequiredArgsConstructor
@Service
public class CartServiceImpl implements CartService {
    private final CartRedisRepository redisRepository;
    private final ProductService productService;

    @Override
    public void addToCart(String sessionId, AddItemDto requestDto) {
        if (!productService.isExist(requestDto.getProductId())) {
            throw new EntityNotFoundException("Product with id " + requestDto.getProductId() + " not found");
        }
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

    public GetCartResponseDto getCart(String sessionId) {
        var cart = redisRepository.findById(sessionId).orElseGet(() -> new Cart(sessionId, new HashMap<>(), 3000));
        var items = cart.getItems();
        var res = new GetCartResponseDto();

        BigDecimal totalPrice = items.entrySet().stream()
                .map(entry -> {
                    var productDetails = productService.getProductDetails(entry.getKey());
                    var cartItem = CartMapper.toCartItem(productDetails, entry.getValue());
                    res.addCartItem(cartItem);
                    return cartItem.getPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity()));
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        res.setTotalPrice(totalPrice);
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