package com.dti.multiwarehouse.stock.dto.response;

import lombok.Getter;

@Getter
public class GetWarehouseAndStockAvailabilityResponseDto {
    private final Long warehouseId;
    private final int stock;

    public GetWarehouseAndStockAvailabilityResponseDto(RetrieveWarehouseAndStockAvailabilityDto dto) {
        this.warehouseId = dto.getWarehouseId();
        this.stock = dto.getStock();
    }
}
