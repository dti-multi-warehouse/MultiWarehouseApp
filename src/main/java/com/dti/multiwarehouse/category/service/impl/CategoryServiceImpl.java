package com.dti.multiwarehouse.category.service.impl;

import com.dti.multiwarehouse.category.dao.Category;
import com.dti.multiwarehouse.category.dto.request.CategoryRequestDto;
import com.dti.multiwarehouse.category.dto.response.GetCategoryResponseDto;
import com.dti.multiwarehouse.category.repository.CategoryRepository;
import com.dti.multiwarehouse.category.service.CategoryService;
import com.dti.multiwarehouse.cloudImageStorage.service.CloudImageStorageService;
import com.dti.multiwarehouse.config.TypeSense;
import com.dti.multiwarehouse.exceptions.ApplicationException;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.typesense.api.FieldTypes;
import org.typesense.model.CollectionSchema;
import org.typesense.model.Field;
import org.typesense.model.SearchParameters;

import java.io.IOException;
import java.util.List;

@RequiredArgsConstructor
@Service
public class CategoryServiceImpl implements CategoryService {

    @Resource
    private final TypeSense typeSense;
    private final CategoryRepository categoryRepository;
    private final CloudImageStorageService cloudImageStorageService;
    String CATEGORY_KEY = "categories";

    @PostConstruct
    public void init() throws Exception {

        try {
//            typeSense.client().collections(CATEGORY_KEY).delete();
            typeSense.client().collections(CATEGORY_KEY).retrieve();
        } catch (Exception e) {
            var collectionSchema = new CollectionSchema();
            collectionSchema.name(CATEGORY_KEY)
                    .addFieldsItem(new Field().name("name").type(FieldTypes.STRING))
                    .addFieldsItem(new Field().name("logoUrl").type(FieldTypes.STRING));
            typeSense.client().collections().create(collectionSchema);
        }
    }

    @Override
    public GetCategoryResponseDto addCategory(CategoryRequestDto requestDto, MultipartFile logo){
        var existing = findSimilarCategoryName(requestDto.getName());
        if (existing != null) {
            throw new ApplicationException(String.format("Category with similar name: %s already exists", existing));
        }
        try {
            var logoUrl = uploadLogo(logo);
            var category = categoryRepository.save(Category.builder()
                            .name(requestDto.getName())
                            .logoUrl(logoUrl)
                            .build()
            );
            typeSense.client()
                    .collections(CATEGORY_KEY)
                    .documents()
                    .upsert(category.toDocument());
            return new GetCategoryResponseDto(category);
        } catch (Exception e) {
            throw new ApplicationException("Failed to create category: " + e.getMessage());
        }
    }

    @Override
    public List<GetCategoryResponseDto> getAllCategories() {
        var categories = categoryRepository.findAllByOrderByIdAsc();
        return categories.stream().map(GetCategoryResponseDto::new).toList();
    }

    @Override
    public GetCategoryResponseDto updateCategory(Long id, CategoryRequestDto requestDto, MultipartFile file) {
        var prevCategory = categoryRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(String.format("Category with id: %s not found", id)));

        try {
            prevCategory.setName(requestDto.getName());
            if (file != null && !file.isEmpty()) {
                cloudImageStorageService.deleteImage(prevCategory.getLogoUrl());
                var logoUrl = uploadLogo(file);
                prevCategory.setLogoUrl(logoUrl);
            }
            var category = categoryRepository.save(prevCategory);
            typeSense.client()
                    .collections(CATEGORY_KEY)
                    .documents()
                    .upsert(category.toDocument());
            return new GetCategoryResponseDto(category);
        } catch (Exception e) {
            throw new ApplicationException(e.getMessage());
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

    private String uploadLogo(MultipartFile logo) throws IOException {
        try {
            return cloudImageStorageService.uploadSvgImage(logo, "categoryLogo");
        } catch (Exception e) {
            throw new ApplicationException("Failed to upload logo");
        }
    }
}
