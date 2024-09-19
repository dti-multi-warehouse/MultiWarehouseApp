package com.dti.multiwarehouse.product.helper;

import com.dti.multiwarehouse.product.dto.response.ProductSearchResponseDto;
import org.typesense.model.SearchResult;

public class ProductMapper {
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
