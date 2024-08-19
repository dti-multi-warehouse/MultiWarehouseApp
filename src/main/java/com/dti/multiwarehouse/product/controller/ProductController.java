package com.dti.multiwarehouse.product.controller;

import com.dti.multiwarehouse.product.dto.request.AddCategoryRequestDto;
import com.dti.multiwarehouse.product.dto.request.AddProductRequestDto;
import com.dti.multiwarehouse.product.dto.request.ProductSummaryRequestDto;
import com.dti.multiwarehouse.product.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    public ResponseEntity<?> addProduct(@Valid @RequestBody AddProductRequestDto requestDto) throws Exception {
        productService.addProduct(requestDto);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<?> displayProducts(
            @RequestParam(defaultValue = "") String query,
            @RequestParam(defaultValue = "") List<Integer> category,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int perPage) throws Exception {
        productService.displayProducts(query, category, page, perPage);
        return ResponseEntity.ok().build();
    }
}
