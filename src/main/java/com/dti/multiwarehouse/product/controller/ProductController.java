package com.dti.multiwarehouse.product.controller;

import com.dti.multiwarehouse.product.dto.request.AddCategoryRequestDto;
import com.dti.multiwarehouse.product.dto.request.AddProductRequestDto;
import com.dti.multiwarehouse.product.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/product")
public class ProductController {
    private final ProductService productService;

    @PostMapping("/category")
    public ResponseEntity<?> createCategory(@Valid @RequestBody AddCategoryRequestDto requestDto) {
        productService.addCategory(requestDto);
        return ResponseEntity.ok().build();
    }

    @PostMapping
    public ResponseEntity<?> addProduct(@Valid @RequestBody AddProductRequestDto requestDto) {
        productService.addProduct(requestDto);
        return ResponseEntity.ok().build();
    }
}
