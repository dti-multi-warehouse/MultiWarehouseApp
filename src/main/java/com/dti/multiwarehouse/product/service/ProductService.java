package com.dti.multiwarehouse.product.service;

import com.dti.multiwarehouse.product.dao.Product;
import com.dti.multiwarehouse.product.dto.request.AddProductRequestDto;
import com.dti.multiwarehouse.product.dto.request.UpdateProductRequestDto;
import com.dti.multiwarehouse.product.dto.response.ProductDetailsResponseDto;
import com.dti.multiwarehouse.product.dto.response.ProductGroupedSearchResponseDto;
import com.dti.multiwarehouse.product.dto.response.ProductSearchResponseDto;
import com.dti.multiwarehouse.product.dto.response.ProductSummaryResponseDto;
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
    List<ProductSummaryResponseDto> getAllProducts();
    void updateSoldAndStock(Long id);
}
