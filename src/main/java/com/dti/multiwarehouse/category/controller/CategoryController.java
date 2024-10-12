package com.dti.multiwarehouse.category.controller;

import com.dti.multiwarehouse.category.dto.request.CategoryRequestDto;
import com.dti.multiwarehouse.category.service.CategoryService;
import com.dti.multiwarehouse.response.Response;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/category")
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping
    public ResponseEntity<?> getAllCategories() {
        var res = categoryService.getAllCategories();
        return Response.success("Categories successfully retrieved", res);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getCategoryById(@PathVariable Long id) {
        var res = categoryService.getCategoryById(id);
        return Response.success("Category successfully retrieved", res);
    }

    @PostMapping
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<?> createCategory(
            @Valid @RequestPart CategoryRequestDto requestDto,
            @RequestPart MultipartFile logo
    ) throws Exception, IOException {
        var res = categoryService.addCategory(requestDto, logo);
        return Response.success("Category successfully created", res);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<?> updateCategory(
            @PathVariable Long id,
            @Valid @RequestPart CategoryRequestDto requestDto,
            @RequestPart MultipartFile logo
    ) throws Exception {
        var res = categoryService.updateCategory(id, requestDto, logo);
        return Response.success("Category successfully updated", res);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<?> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return Response.success("Category successfully deleted");
    }
}
