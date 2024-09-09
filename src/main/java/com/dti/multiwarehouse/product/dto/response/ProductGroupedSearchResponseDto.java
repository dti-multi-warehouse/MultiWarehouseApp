package com.dti.multiwarehouse.product.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.typesense.model.SearchGroupedHit;

import java.util.List;

@Data
@AllArgsConstructor
public class ProductGroupedSearchResponseDto {
    private List<SearchGroupedHit> featuredProducts;
}
