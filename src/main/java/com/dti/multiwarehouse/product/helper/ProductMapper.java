package com.dti.multiwarehouse.product.helper;

import com.dti.multiwarehouse.category.dao.Category;
import com.dti.multiwarehouse.product.dao.Product;
import com.dti.multiwarehouse.product.dto.request.AddProductRequestDto;
import com.dti.multiwarehouse.product.dto.response.ProductDetailsResponseDto;
import com.dti.multiwarehouse.product.dto.response.ProductSearchResponseDto;
import com.dti.multiwarehouse.product.dto.response.ProductSummaryResponseDto;
import org.typesense.model.SearchResult;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class ProductMapper {
    public static Product toEntity(AddProductRequestDto requestDto, Category category, List<String> urls) {
        return Product
                .builder()
                .name(requestDto.getName())
                .description(requestDto.getDescription())
                .price(requestDto.getPrice())
                .stock(0)
                .category(category)
                .sold(0)
                .imageUrls(new HashSet<>(urls))
                .build();
    }

    public static HashMap<String, Object> toDocument(Product product) {
        HashMap<String, Object> document = new HashMap<>();
        document.put("id", product.getId().toString());
        document.put("name", product.getName());
        document.put("description", product.getDescription());
        document.put("price", product.getPrice());
        document.put("stock", product.getStock());
        document.put("category", product.getCategory().getName());
        document.put("sold", product.getSold());
        document.put("thumbnail", product.getImageUrls().stream().findFirst().orElse(null));
        return document;
    }

    public static ProductSummaryResponseDto toSummaryResponseDto(Product product) {
        return ProductSummaryResponseDto.builder()
                .id(product.getId())
                .name(product.getName())
                .price(product.getPrice())
                .stock(product.getStock())
                .category(product.getCategory().getName())
                .thumbnail(product.getImageUrls().stream().findFirst().orElse(null))
                .build();
    }

    public static ProductDetailsResponseDto toDetailsResponseDto(Product product) {
        return ProductDetailsResponseDto.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .stock(product.getStock())
                .category(product.getCategory().getName())
                .imageUrls(product.getImageUrls())
                .build();
    }

    public static ProductSearchResponseDto toSearchResponseDto(SearchResult searchResult) {
        return ProductSearchResponseDto.builder()
                .found(searchResult.getFound())
                .page(searchResult.getPage())
                .perPage(searchResult.getRequestParams().getPerPage())
                .totalPage((searchResult.getFound() / searchResult.getRequestParams().getPerPage()) + 1)
                .hits(searchResult.getHits())
                .build();
    }
}
