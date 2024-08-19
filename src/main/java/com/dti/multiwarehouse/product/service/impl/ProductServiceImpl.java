package com.dti.multiwarehouse.product.service.impl;

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
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    @Override
    public Page<ProductSummaryResponseDto> displayProducts(ProductSummaryRequestDto requestDto, Pageable pageable) {
        return null;
    }

    @Override
    public ProductDetailsResponseDto getProductDetails(Long id) {
        return null;
    }

    @Override
    public ProductDetailsResponseDto addProduct(AddProductRequestDto requestDto) {
        Product product = new Product();
        product.setName(requestDto.getName());
        product.setDescription(requestDto.getDescription());
        product.setPrice(requestDto.getPrice());
        product.setStock(requestDto.getStock());
        product.setCategoryId(requestDto.getCategoryId());
        productRepository.save(product);
        return null;
    }

    @Override
    public void addCategory(AddCategoryRequestDto requestDto) {
        Category category = new Category();
        category.setName(requestDto.getName());
        categoryRepository.save(category);
    }
}
