package com.dti.multiwarehouse.product.dto.response;

import lombok.Getter;
import org.typesense.model.SearchResult;

import java.util.List;

@Getter
public class FilteredProductIdDto {
    private final int page;
    private final int totalPage;
    private final List<Long> ids;

    public FilteredProductIdDto(SearchResult searchResult) {
        var stringIds = searchResult.getHits().stream().map(hit -> hit.getDocument().get("id").toString()).toList();
        this.page = searchResult.getPage();
        this.totalPage = (searchResult.getFound() / searchResult.getRequestParams().getPerPage()) + 1;
        this.ids = stringIds.stream().map(Long::parseLong).toList();
    }
}
