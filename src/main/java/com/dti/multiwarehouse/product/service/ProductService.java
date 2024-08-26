package com.dti.multiwarehouse.product.service;

import com.dti.multiwarehouse.product.dto.request.AddProductRequestDto;
import com.dti.multiwarehouse.product.dto.response.ProductDetailsResponseDto;
import com.dti.multiwarehouse.product.dto.response.ProductSearchResponseDto;
import com.dti.multiwarehouse.product.dto.response.ProductSummaryResponseDto;

import java.util.List;

public interface ProductService {
    ProductSearchResponseDto displayProducts(String query, List<String> category, int page, int perPage) throws Exception;
    ProductDetailsResponseDto getProductDetails(Long id);
    ProductSummaryResponseDto addProduct(AddProductRequestDto requestDto) throws Exception;
}
