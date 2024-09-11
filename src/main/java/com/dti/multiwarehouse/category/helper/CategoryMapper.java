package com.dti.multiwarehouse.category.helper;

import com.dti.multiwarehouse.category.dao.Category;
import com.dti.multiwarehouse.category.dto.request.CategoryRequestDto;
import com.dti.multiwarehouse.category.dto.response.CategoryResponseDto;

import java.util.HashMap;

public class CategoryMapper {
    public static Category toEntity(CategoryRequestDto requestDto) {
        return Category.builder().name(requestDto.getName()).build();
    }

    public static Category toEntity(Long id, CategoryRequestDto requestDto) {
        return Category.builder().id(id).name(requestDto.getName()).build();
    }

    public static CategoryResponseDto toResponseDto(Category category) {
        return CategoryResponseDto.builder().id(category.getId()).name(category.getName()).build();
    }

    public static HashMap<String, Object> toDocument(Category category) {
        HashMap<String, Object> document = new HashMap<>();
        document.put("id", category.getId().toString());
        document.put("name", category.getName());
        return document;
    }
}
