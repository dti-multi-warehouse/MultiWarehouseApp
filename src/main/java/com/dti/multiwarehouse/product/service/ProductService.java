package com.dti.multiwarehouse.product.service;

import com.dti.multiwarehouse.product.dao.Product;
import com.dti.multiwarehouse.product.dto.request.AddProductRequestDto;
import com.dti.multiwarehouse.product.dto.request.UpdateProductRequestDto;
import com.dti.multiwarehouse.product.dto.response.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ProductService {
    ProductGroupedSearchResponseDto displayFeaturedProducts() throws Exception;
    ProductSearchResponseDto displayProducts(String query, List<String> category, int page, int perPage) throws Exception;
    ProductDetailsResponseDto getProductDetails(Long id);
    ProductSummaryResponseDto addProduct(AddProductRequestDto requestDto, List<MultipartFile> images) throws Exception;
    ProductSummaryResponseDto updateProduct(Long id, UpdateProductRequestDto requestDto, List<MultipartFile> images) throws Exception;
    void deleteProduct(Long id);
    boolean isExist(Long id);
    Product findProductById(Long id);
    GetDashboardProductDto getAllProducts(String query, int page);
    int getProductPrice(Long productId);
    void updateSoldAndStock(Long id);
}
