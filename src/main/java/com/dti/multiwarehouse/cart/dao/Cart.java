package com.dti.multiwarehouse.cart.dao;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

import java.util.Map;

@Data
@AllArgsConstructor
@RedisHash("AlphaMarch:cart")
public class Cart {
    @Id
    private String id;
    private Map<Long, Integer> items;
    @TimeToLive
    private long duration;

    public void addItem(Long productId, Integer quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than 0");
        }
        if (!items.containsKey(productId)) {
            items.put(productId, quantity);
        } else {
            items.replace(productId, items.get(productId) + quantity);
        }
    }

    public void removeItem(Long productId) {
        items.remove(productId);
    }

    public void incrementQuantity(Long productId) {
        addItem(productId, 1);
    }

    public void decrementQuantity(Long productId) {
        if (items.containsKey(productId)) {
            var quantity = items.get(productId) - 1;
            if (quantity <= 0) {
                removeItem(productId);
            } else {
                items.replace(productId, quantity);
            }
        }
    }

}
