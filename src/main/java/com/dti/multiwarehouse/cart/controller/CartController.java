package com.dti.multiwarehouse.cart.controller;

import com.dti.multiwarehouse.cart.dto.AddItemDto;
import com.dti.multiwarehouse.cart.service.CartService;
import com.dti.multiwarehouse.response.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/cart")
public class CartController {
    private final CartService cartService;

    @GetMapping
    public ResponseEntity<?> getCartItems(@AuthenticationPrincipal Jwt jwt) {
        if (jwt == null) {
            throw new AuthenticationCredentialsNotFoundException("Invalid token");
        }
        var res = cartService.getCart(jwt.getTokenValue());
        return Response.success("Cart retrieved successfully", res);
    }

    @PostMapping
    public ResponseEntity<?> addItemToCart(@AuthenticationPrincipal Jwt jwt, @RequestBody AddItemDto addItemDto) {
        cartService.addToCart(jwt.getTokenValue(), addItemDto);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/increment/{id}")
    public ResponseEntity<?> incrementItem(@AuthenticationPrincipal Jwt jwt, @PathVariable Long id) {
        cartService.incrementQuantity(jwt.getTokenValue(), id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/decrement/{id}")
    public ResponseEntity<?> decrementItem(@AuthenticationPrincipal Jwt jwt, @PathVariable Long id) {
        cartService.decrementQuantity(jwt.getTokenValue(), id);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> removeItemFromCart(@AuthenticationPrincipal Jwt jwt, @PathVariable Long id) {
        cartService.removeFromCart(jwt.getTokenValue(), id);
        return ResponseEntity.ok().build();
    }
}
