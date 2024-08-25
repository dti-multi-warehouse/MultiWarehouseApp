package com.dti.multiwarehouse.product.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.typesense.model.SearchResultHit;

import java.util.List;

@Getter
@AllArgsConstructor
@Builder
public class ProductSearchResponseDto {
    private int found;
    private int page;
    private int perPage;
    private int totalPage;
    private List<SearchResultHit> hits;
}
