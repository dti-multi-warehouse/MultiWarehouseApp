package com.dti.multiwarehouse.stock.dto.response;

import lombok.Getter;

@Getter
public class GetProductAndStockAvailabilityDto {
    private final Long productId;
    private final String name;
    private final int stock;
    private final String thumbnail;

    public GetProductAndStockAvailabilityDto(RetrieveProductAndStockAvailabilityDto dto) {
        this.productId = dto.getId();
        this.name = dto.getName();
        this.stock = dto.getStock();
        this.thumbnail = dto.getThumbnail();
    }
}
