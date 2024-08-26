package com.dti.multiwarehouse.category.service.impl;

import com.dti.multiwarehouse.category.dao.Category;
import com.dti.multiwarehouse.category.dto.request.CategoryRequestDto;
import com.dti.multiwarehouse.category.dto.response.CategoryResponseDto;
import com.dti.multiwarehouse.category.helper.CategoryMapper;
import com.dti.multiwarehouse.category.repository.CategoryRepository;
import com.dti.multiwarehouse.category.service.CategoryService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@RequiredArgsConstructor
@Service
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    @Override
    public CategoryResponseDto addCategory(CategoryRequestDto requestDto) {
        var category = categoryRepository.save(CategoryMapper.toEntity(requestDto));
        return CategoryMapper.toResponseDto(category);
    }

    @Override
    public List<CategoryResponseDto> getAllCategories() {
        var categories = categoryRepository.findAll();
        return categories.stream().map(CategoryMapper::toResponseDto).toList();
    }

    @Override
    public CategoryResponseDto updateCategory(Long id, CategoryRequestDto requestDto) {
        var isExist = categoryRepository.existsById(id);
        if (!isExist) {
            throw new EntityNotFoundException("Category with id " + id + " not found");
        }
        var category = categoryRepository.save(CategoryMapper.toEntity(id, requestDto));
        return CategoryMapper.toResponseDto(category);
    }

    @Override
    public void deleteCategory(Long id) {
        var category = categoryRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Category with id " + id + " not found"));
        category.setDeletedAt(Instant.now());
        categoryRepository.save(category);
    }

    @Override
    public Category getCategoryById(Long id) {
        return categoryRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Category with id " + id + " not found"));
    }
}
