package com.dti.multiwarehouse.category.dto.response;

import com.dti.multiwarehouse.category.dao.Category;
import lombok.Getter;

@Getter
public class GetCategoryResponseDto {
    private final Long id;
    private final String name;
    private final String logoUrl;

    public GetCategoryResponseDto(Category category) {
        this.id = category.getId();
        this.name = category.getName();
        this.logoUrl = category.getLogoUrl();
    }
}
