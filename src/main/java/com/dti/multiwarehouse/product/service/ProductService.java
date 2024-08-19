package com.dti.multiwarehouse.product.service;

import com.dti.multiwarehouse.product.dto.request.AddCategoryRequestDto;
import com.dti.multiwarehouse.product.dto.request.AddProductRequestDto;
import com.dti.multiwarehouse.product.dto.request.ProductSummaryRequestDto;
import com.dti.multiwarehouse.product.dto.response.ProductDetailsResponseDto;
import com.dti.multiwarehouse.product.dto.response.ProductSummaryResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ProductService {
    Page<ProductSummaryResponseDto> displayProducts(String query, List<Integer> category, int page, int perPage) throws Exception;
    ProductDetailsResponseDto getProductDetails(Long id);
    ProductDetailsResponseDto addProduct(AddProductRequestDto requestDto) throws Exception;
    void addCategory(AddCategoryRequestDto requestDto);
}
