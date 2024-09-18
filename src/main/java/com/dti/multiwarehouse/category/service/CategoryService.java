package com.dti.multiwarehouse.category.service;

import com.dti.multiwarehouse.category.dao.Category;
import com.dti.multiwarehouse.category.dto.request.CategoryRequestDto;
import com.dti.multiwarehouse.category.dto.response.CategoryResponseDto;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface CategoryService {
    CategoryResponseDto addCategory(CategoryRequestDto requestDto, MultipartFile logo) throws Exception, IOException;
    List<CategoryResponseDto> getAllCategories();
    CategoryResponseDto updateCategory(Long id, CategoryRequestDto requestDto, MultipartFile logo) throws Exception;
    void deleteCategory(Long id);
    Category getCategoryById(Long id);
}
