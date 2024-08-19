package com.dti.multiwarehouse.product.service;

import com.dti.multiwarehouse.product.dto.request.ProductSummaryRequestDto;
import com.dti.multiwarehouse.product.dto.response.ProductDetailsResponseDto;
import com.dti.multiwarehouse.product.dto.response.ProductSummaryResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductService {
    Page<ProductSummaryResponseDto> displayProducts(ProductSummaryRequestDto requestDto, Pageable pageable);
    ProductDetailsResponseDto getProductDetails(Long id);
}
