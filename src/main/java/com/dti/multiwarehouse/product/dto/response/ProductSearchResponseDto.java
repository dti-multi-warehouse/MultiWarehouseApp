package com.dti.multiwarehouse.product.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.typesense.model.SearchResult;
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

    public ProductSearchResponseDto(SearchResult searchResult) {
        this.found = searchResult.getFound();
        this.page = searchResult.getPage();
        this.perPage = searchResult.getRequestParams().getPerPage();
        this.totalPage = (searchResult.getFound() / searchResult.getRequestParams().getPerPage()) + 1;
        this.hits = searchResult.getHits();
    }
}
