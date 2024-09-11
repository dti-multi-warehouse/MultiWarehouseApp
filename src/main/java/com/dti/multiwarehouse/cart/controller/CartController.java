package com.dti.multiwarehouse.cart.controller;

import com.dti.multiwarehouse.cart.dto.AddItemDto;
import com.dti.multiwarehouse.cart.service.CartService;
import com.dti.multiwarehouse.response.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/cart")
public class CartController {
    private final CartService cartService;

    @GetMapping
    public ResponseEntity<?> getCartItems() {
        var res = cartService.getCart("ehehehe");
        return Response.success("Cart retrieved successfully", res);
    }

    @PostMapping
    public ResponseEntity<?> addItemToCart(@RequestBody AddItemDto addItemDto) {
        cartService.addToCart("ehehehe", addItemDto);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/increment/{id}")
    public ResponseEntity<?> incrementItem(@PathVariable Long id) {
        cartService.incrementQuantity("ehehehe", id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/decrement/{id}")
    public ResponseEntity<?> decrementItem(@PathVariable Long id) {
        cartService.decrementQuantity("ehehehe", id);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> removeItemFromCart(@PathVariable Long id) {
        cartService.removeFromCart("ehehehe", id);
        return ResponseEntity.ok().build();
    }
}
