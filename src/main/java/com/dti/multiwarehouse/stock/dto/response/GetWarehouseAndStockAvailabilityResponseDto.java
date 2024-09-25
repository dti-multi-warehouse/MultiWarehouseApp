package com.dti.multiwarehouse.stock.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GetWarehouseAndStockAvailabilityResponseDto {
    private Long warehouseId;
    private int stock;

    public static GetWarehouseAndStockAvailabilityResponseDto fromDto (RetrieveWarehouseAndStockAvailabilityDto dto) {
        return GetWarehouseAndStockAvailabilityResponseDto.builder()
                .warehouseId(dto.getWarehouseId())
                .stock(dto.getStock())
                .build();
    }
}
