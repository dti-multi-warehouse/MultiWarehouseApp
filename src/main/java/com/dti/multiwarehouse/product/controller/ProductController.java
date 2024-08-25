package com.dti.multiwarehouse.product.controller;

import com.dti.multiwarehouse.product.dto.request.AddCategoryRequestDto;
import com.dti.multiwarehouse.product.dto.request.AddProductRequestDto;
import com.dti.multiwarehouse.product.service.ProductService;
import com.dti.multiwarehouse.response.Response;
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
        return Response.success("Category successfully created");
    }

    @PostMapping
    public ResponseEntity<?> addProduct(@Valid @RequestBody AddProductRequestDto requestDto) throws Exception {
        var res = productService.addProduct(requestDto);
        return Response.success("Product successfully added", res);
    }

    @GetMapping
    public ResponseEntity<?> displayProducts(
            @RequestParam(defaultValue = "") String query,
            @RequestParam(defaultValue = "") List<Integer> category,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int perPage) throws Exception {
        var res = productService.displayProducts(query, category, page, perPage);
        return Response.success("Products successfully retrieved", res);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getProductDetails(@PathVariable Long id) {
        var res = productService.getProductDetails(id);
        return Response.success("Product successfully retrieved", res);
    }
}
