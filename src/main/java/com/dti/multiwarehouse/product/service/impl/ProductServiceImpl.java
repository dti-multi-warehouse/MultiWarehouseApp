package com.dti.multiwarehouse.product.service.impl;

import com.dti.multiwarehouse.category.service.CategoryService;
import com.dti.multiwarehouse.cloudImageStorage.service.CloudImageStorageService;
import com.dti.multiwarehouse.config.TypeSense;
import com.dti.multiwarehouse.exceptions.ApplicationException;
import com.dti.multiwarehouse.helper.EntityUpdateUtil;
import com.dti.multiwarehouse.product.dao.Product;
import com.dti.multiwarehouse.product.dto.request.AddProductRequestDto;
import com.dti.multiwarehouse.product.dto.request.UpdateProductRequestDto;
import com.dti.multiwarehouse.product.dto.response.*;
import com.dti.multiwarehouse.product.repository.ProductRepository;
import com.dti.multiwarehouse.product.service.ProductService;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.typesense.api.FieldTypes;
import org.typesense.model.*;

import java.io.IOException;
import java.util.*;

@RequiredArgsConstructor
@Service
public class ProductServiceImpl implements ProductService {
    @Resource
    private final TypeSense typeSense;
    private final ProductRepository productRepository;

    private final CategoryService categoryService;
    private final CloudImageStorageService cloudImageStorageService;

    String PRODUCT_KEY = "products";

    @PostConstruct
    public void init() throws Exception {
        try {
//            typeSense.client().collections(PRODUCT_KEY).delete();
            typeSense.client().collections(PRODUCT_KEY).retrieve();
        } catch (Exception e) {
            CollectionSchema productCollectionSchema = new CollectionSchema();
            productCollectionSchema.name(PRODUCT_KEY)
                    .addFieldsItem(new Field().name("id").type(FieldTypes.INT64))
                    .addFieldsItem(new Field().name("name").type(FieldTypes.STRING))
                    .addFieldsItem(new Field().name("description").type(FieldTypes.STRING))
                    .addFieldsItem(new Field().name("price").type(FieldTypes.FLOAT))
                    .addFieldsItem(new Field().name("category").type(FieldTypes.STRING).facet(true))
                    .addFieldsItem(new Field().name("sold").type(FieldTypes.INT32))
                    .addFieldsItem(new Field().name("thumbnail").type(FieldTypes.STRING))
                    .defaultSortingField("sold");
            CollectionResponse collectionResponse = typeSense.client().collections().create(productCollectionSchema);
        }
    }

    @Override
    public ProductGroupedSearchResponseDto displayFeaturedProducts() throws Exception {
        var searchParameters = new SearchParameters()
                .q("*")
                .queryBy("name")
                .groupBy("category")
                .groupLimit(6);
        var searchResult = typeSense.client().collections(PRODUCT_KEY).documents().search(searchParameters);
        searchResult.getGroupedHits().forEach(System.out::println);
        return new ProductGroupedSearchResponseDto(searchResult.getGroupedHits());
    }

    @Override
    public ProductSearchResponseDto displayProducts(String query, List<String> category, int page, int perPage) throws Exception {
        var searchParameters = new SearchParameters()
                .q(query)
                .queryBy("name,description")
                .page(page)
                .perPage(perPage);
        if (!category.isEmpty()) {
            searchParameters.filterBy("category:" + category);
        }
        var searchResult = typeSense.client().collections(PRODUCT_KEY).documents().search(searchParameters);
        return new ProductSearchResponseDto(searchResult);
    }

    @Override
    public ProductDetailsResponseDto getProductDetails(Long id) {
        var product = productRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Product with id " + id + " not found"));
        return new ProductDetailsResponseDto(product);
    }

    @Override
    public ProductSummaryResponseDto addProduct(AddProductRequestDto requestDto, List<MultipartFile> images) throws Exception {
        var category = categoryService.getCategoryById(requestDto.getCategoryId());
        var imageUrls = uploadImages(images);
        var product = productRepository.save(requestDto.toProduct(category, imageUrls));

        typeSense.client().collections(PRODUCT_KEY).documents().upsert(product.toDocument());

        return new ProductSummaryResponseDto(product);
    }

    @Override
    public ProductSummaryResponseDto updateProduct(Long id, UpdateProductRequestDto requestDto, List<MultipartFile> images) throws Exception {
        var product = productRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Product with id " + id + " not found"));
        EntityUpdateUtil.updateEntityFromDto(product, requestDto);

        if (requestDto.getCategoryId() != null) {
            var category = categoryService.getCategoryById(requestDto.getCategoryId());
            product.setCategory(category);
        }

        var currentImageUrls = new ArrayList<>(product.getImageUrls());
        List<String> deletedImageUrls;

        if (requestDto.getPrevImages() != null) {
            deletedImageUrls = new ArrayList<>(currentImageUrls);
            deletedImageUrls.removeAll(requestDto.getPrevImages());
        } else {
            deletedImageUrls = currentImageUrls;
        }

        deleteImages(deletedImageUrls);
        for (var imageUrl : deletedImageUrls) {
            product.removeImageUrl(imageUrl);
        }

        if (images != null) {
            var imageUrls = uploadImages(images);
            for (var imageUrl : imageUrls) {
                product.addImageUrl(imageUrl);
            }
        }

        var p = productRepository.save(product);

        try {
            typeSense.client().collections(PRODUCT_KEY).documents().upsert(p.toDocument());
        } catch (Exception e) {
            throw new ApplicationException(e.getMessage());
        }

        return new ProductSummaryResponseDto(p);
    }

    @Override
    public void deleteProduct(Long id) {
        try {
            productRepository.deleteById(id);
            typeSense.client().collections(PRODUCT_KEY)
                    .documents(id.toString())
                    .delete();
        } catch (Exception e) {
           throw new ApplicationException("Failed to delete product with id: " + id);
        }

    }

    @Override
    public List<ProductSummaryResponseDto> getAllProducts() {
        return productRepository.findAllByOrderByIdAsc().stream()
                .map(ProductSummaryResponseDto::new)
                .toList();
    }

    @Override
    public int getProductPrice(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new EntityNotFoundException("Product with id " + productId + " not found"));

        return product.getPrice();
    }

    private List<String> uploadImages(List<MultipartFile> images) throws IOException {
        var imageUrls = new ArrayList<String>();

        try {
            for (var image : images) {
                var url = cloudImageStorageService.uploadImage(image, "productImage");
                if (url != null && !url.isEmpty()) {
                    imageUrls.add(url);
                } else {
                    throw new IOException("Failed to upload image " + image.getOriginalFilename());
                }
            }
        } catch (IOException e) {
            for (var url : imageUrls) {
                cloudImageStorageService.deleteImage(url);
            }
            throw new ApplicationException("Failed to upload image");
        }

        return imageUrls;

    }

    private void deleteImages(List<String> imageUrls) {
        try {
            for (var imageUrl : imageUrls) {
                cloudImageStorageService.deleteImage(imageUrl);
            }
        } catch (IOException e) {
            throw new ApplicationException("Failed to delete image: " + imageUrls);
        }

    }

    @Override
    public boolean isExist(Long id) {
        return productRepository.existsById(id);
    }

    @Override
    public Product findProductById(Long id) {
        return productRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Product with id " + id + " not found"));
    }

    @Transactional
    @Override
    public void updateSoldAndStock(Long productId) {
        productRepository.recalculateSold(productId);
        productRepository.recalculateStock(productId);
        var res = productRepository.getSoldAndStock(productId);
        var product = productRepository.findById(productId).orElseThrow(() -> new EntityNotFoundException("Product with id " + productId + " not found"));
        product.setSold(res.getSold());
        product.setStock(res.getStock());
        try {
            typeSense.client().collections(PRODUCT_KEY).documents().upsert(product.toDocument());
        } catch (Exception e) {
            throw new ApplicationException(e.getMessage());
        }
    }
}
