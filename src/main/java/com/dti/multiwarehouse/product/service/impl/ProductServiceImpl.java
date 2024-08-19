package com.dti.multiwarehouse.product.service.impl;

import com.dti.multiwarehouse.config.TypeSense;
import com.dti.multiwarehouse.product.dao.Category;
import com.dti.multiwarehouse.product.dao.Product;
import com.dti.multiwarehouse.product.dto.request.AddCategoryRequestDto;
import com.dti.multiwarehouse.product.dto.request.AddProductRequestDto;
import com.dti.multiwarehouse.product.dto.request.ProductSummaryRequestDto;
import com.dti.multiwarehouse.product.dto.response.ProductDetailsResponseDto;
import com.dti.multiwarehouse.product.dto.response.ProductSummaryResponseDto;
import com.dti.multiwarehouse.product.repository.CategoryRepository;
import com.dti.multiwarehouse.product.repository.ProductRepository;
import com.dti.multiwarehouse.product.service.ProductService;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.typesense.api.FieldTypes;
import org.typesense.model.*;

import java.util.HashMap;

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
//                .defaultSortingField("name")
                    .addFieldsItem(new Field().name("id").type(FieldTypes.INT64))
                    .addFieldsItem(new Field().name("name").type(FieldTypes.STRING))
                    .addFieldsItem(new Field().name("description").type(FieldTypes.STRING))
                    .addFieldsItem(new Field().name("price").type(FieldTypes.FLOAT))
                    .addFieldsItem(new Field().name("categoryId").type(FieldTypes.INT64));
            CollectionResponse collectionResponse = typeSense.client().collections().create(productCollectionSchema);
        }
    }

    @Override
    public Page<ProductSummaryResponseDto> displayProducts(ProductSummaryRequestDto requestDto, Pageable pageable) throws Exception {
        var searchParameters = new SearchParameters()
                .q("GeiSHa")
                .queryBy("name,description")
                .page(0)
                .perPage(5);
        var searchResult = typeSense.client().collections("products").documents().search(searchParameters);
        searchResult.getHits().forEach(searchResultHit -> System.out.println(searchResultHit.getDocument()));
        return null;
    }

    @Override
    public ProductDetailsResponseDto getProductDetails(Long id) {
        return null;
    }

    @Override
    public ProductDetailsResponseDto addProduct(AddProductRequestDto requestDto) throws Exception {
        var product = productRepository.save(requestDto.toProduct());

        typeSense.client().collections("products").documents().create(product.toDocument());

        return null;
    }

    @Override
    public void addCategory(AddCategoryRequestDto requestDto) {
        Category category = new Category();
        category.setName(requestDto.getName());
        categoryRepository.save(category);
    }
}
