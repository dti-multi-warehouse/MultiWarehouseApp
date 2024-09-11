package com.dti.multiwarehouse.category.service.impl;

import com.dti.multiwarehouse.category.dao.Category;
import com.dti.multiwarehouse.category.dto.request.CategoryRequestDto;
import com.dti.multiwarehouse.category.dto.response.CategoryResponseDto;
import com.dti.multiwarehouse.category.helper.CategoryMapper;
import com.dti.multiwarehouse.category.repository.CategoryRepository;
import com.dti.multiwarehouse.category.service.CategoryService;
import com.dti.multiwarehouse.config.TypeSense;
import com.dti.multiwarehouse.exceptions.ApplicationException;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.typesense.api.FieldTypes;
import org.typesense.model.CollectionSchema;
import org.typesense.model.Field;
import org.typesense.model.SearchParameters;

import java.util.List;

@RequiredArgsConstructor
@Service
public class CategoryServiceImpl implements CategoryService {

    @Resource
    private final TypeSense typeSense;
    private final CategoryRepository categoryRepository;
    String CATEGORY_KEY = "categories";
    @PostConstruct
    public void init() throws Exception {

        try {
//            typeSense.client().collections(CATEGORY_KEY).delete();
            typeSense.client().collections(CATEGORY_KEY).retrieve();
        } catch (Exception e) {
            var collectionSchema = new CollectionSchema();
            collectionSchema.name(CATEGORY_KEY)
                    .addFieldsItem(new Field().name("name").type(FieldTypes.STRING));
            typeSense.client().collections().create(collectionSchema);
        }
    }

    @Override
    public CategoryResponseDto addCategory(CategoryRequestDto requestDto){
        var existing = findSimilarCategoryName(requestDto.getName());
        if (existing != null) {
            throw new ApplicationException(String.format("Category with similar name: %s already exists", existing));
        }
        try {
            var category = categoryRepository.save(CategoryMapper.toEntity(requestDto));
            typeSense.client().collections(CATEGORY_KEY).documents().create(CategoryMapper.toDocument(category));
            return CategoryMapper.toResponseDto(category);
        } catch (Exception e) {
            throw new ApplicationException("Failed to create category: " + e.getMessage());
        }
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
        try {
            var category = categoryRepository.save(CategoryMapper.toEntity(id, requestDto));
            typeSense.client().collections(CATEGORY_KEY)
                    .documents(category.getId().toString())
                    .update(CategoryMapper.toDocument(category));
            return CategoryMapper.toResponseDto(category);
        } catch (Exception e) {
            throw new ApplicationException("Failed to update category");
        }

    }

    @Override
    public void deleteCategory(Long id) {
        try {
            categoryRepository.deleteById(id);
            typeSense.client().collections(CATEGORY_KEY).documents(id.toString()).delete();
        } catch (Exception e) {
            throw new ApplicationException("Failed to delete category");
        }

    }

    @Override
    public Category getCategoryById(Long id) {
        return categoryRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Category with id " + id + " not found"));
    }

    private String  findSimilarCategoryName(String name) {
        var searchParams = new SearchParameters()
                .q(name)
                .queryBy("name");
        try {
            var searchResult = typeSense.client().collections(CATEGORY_KEY).documents().search(searchParams);
            var hit = searchResult.getHits().stream().findFirst().orElse(null);
            if (hit != null) {
                return hit.getDocument().get("name").toString();
            }
            return null;
        } catch (Exception e) {
            throw new ApplicationException("Failed to search categories: " + e.getMessage());
        }

    }
}
