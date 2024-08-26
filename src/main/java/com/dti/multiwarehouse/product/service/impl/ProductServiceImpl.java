package com.dti.multiwarehouse.product.service.impl;

import com.dti.multiwarehouse.config.TypeSense;
import com.dti.multiwarehouse.category.dao.Category;
import com.dti.multiwarehouse.product.dto.request.AddProductRequestDto;
import com.dti.multiwarehouse.product.dto.response.ProductDetailsResponseDto;
import com.dti.multiwarehouse.product.dto.response.ProductSearchResponseDto;
import com.dti.multiwarehouse.product.dto.response.ProductSummaryResponseDto;
import com.dti.multiwarehouse.product.helper.ProductMapper;
import com.dti.multiwarehouse.category.repository.CategoryRepository;
import com.dti.multiwarehouse.product.repository.ProductRepository;
import com.dti.multiwarehouse.product.service.ProductService;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.typesense.api.FieldTypes;
import org.typesense.model.*;

import java.util.List;

@RequiredArgsConstructor
@Service
public class ProductServiceImpl implements ProductService {
    @Resource
    private final TypeSense typeSense;
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    @PostConstruct
    public void init() throws Exception {
        try {
            typeSense.client().collections("products").retrieve();
        } catch (Exception e) {
            CollectionSchema productCollectionSchema = new CollectionSchema();
            productCollectionSchema.name("products")
                    .addFieldsItem(new Field().name("id").type(FieldTypes.INT64))
                    .addFieldsItem(new Field().name("name").type(FieldTypes.STRING))
                    .addFieldsItem(new Field().name("description").type(FieldTypes.STRING))
                    .addFieldsItem(new Field().name("price").type(FieldTypes.FLOAT))
                    .addFieldsItem(new Field().name("category").type(FieldTypes.STRING))
                    .addFieldsItem(new Field().name("sold").type(FieldTypes.INT32))
                    .defaultSortingField("sold");
            CollectionResponse collectionResponse = typeSense.client().collections().create(productCollectionSchema);
        }
    }

    @Override
    public ProductSearchResponseDto displayProducts(String query, List<String> category, int page, int perPage) throws Exception {
        var searchParameters = new SearchParameters()
                .q(query)
                .queryBy("name,description")
//                .filterBy(category.isEmpty() ? "category:*" : "category:" + category)
                .page(page)
                .perPage(perPage);
        System.out.println(category.isEmpty());
        if (!category.isEmpty()) {
            searchParameters.filterBy("category:" + category);
        }
        var searchResult = typeSense.client().collections("products").documents().search(searchParameters);
        return ProductMapper.toSearchResponseDto(searchResult);
    }

    @Override
    public ProductDetailsResponseDto getProductDetails(Long id) {
        var product = productRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Product with id " + id + " not found"));
        return ProductMapper.toDetailsResponseDto(product);
    }

    @Override
    public ProductSummaryResponseDto addProduct(AddProductRequestDto requestDto) throws Exception {
        var category = getCategory(requestDto.getCategoryId());
        var product = productRepository.save(ProductMapper.toEntity(requestDto, category));

        typeSense.client().collections("products").documents().create(ProductMapper.toDocument(product));

        return ProductMapper.toSummaryResponseDto(product);
    }

    private Category getCategory(Long categoryId) {
        return categoryRepository.findById(categoryId).orElseThrow(() -> new EntityNotFoundException("Category with id " + categoryId + " not found"));
    }
}
