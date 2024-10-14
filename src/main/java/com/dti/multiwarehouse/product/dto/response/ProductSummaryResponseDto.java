package com.dti.multiwarehouse.product.dto.response;

import com.dti.multiwarehouse.product.dao.Product;
import lombok.Getter;

@Getter
public class ProductSummaryResponseDto {
    private final Long id;
    private final String name;
    private final int price;
    private final int stock;
    private final String category;
    private final String thumbnail;

    public ProductSummaryResponseDto(Product product) {
        this.id = product.getId();
        this.name = product.getName();
        this.price = product.getPrice();
        this.stock = product.getStock();
        this.category = product.getCategory() != null ? product.getCategory().getName() : "Uncategorized";
        this.thumbnail = product.getImageUrls().getFirst();
    }
}
