package com.dti.multiwarehouse.product.controller;

import com.dti.multiwarehouse.product.dto.request.AddProductRequestDto;
import com.dti.multiwarehouse.product.dto.request.UpdateProductRequestDto;
import com.dti.multiwarehouse.product.service.ProductService;
import com.dti.multiwarehouse.response.Response;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/product")
public class ProductController {
    private final ProductService productService;

    @PostMapping
    public ResponseEntity<?> addProduct(
            @Valid @RequestPart AddProductRequestDto product,
            @RequestPart(required = true) List<MultipartFile> images
            ) throws Exception {
        var res = productService.addProduct(product, images);
        return Response.success("Product successfully added", res);
    }

    @GetMapping
    public ResponseEntity<?> displayProducts(
            @RequestParam(defaultValue = "") String query,
            @RequestParam(defaultValue = "") List<String> category,
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

    @PutMapping("/{id}")
    public ResponseEntity<?> updateProduct(@PathVariable Long id,
                                           @RequestPart UpdateProductRequestDto requestDto,
                                           @RequestPart(required = false) List<MultipartFile> images
    ) throws Exception {
        var res = productService.updateProduct(id, requestDto, images);
        return Response.success("Product successfully updated", res);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return Response.success("Product successfully deleted", id);
    }
}
