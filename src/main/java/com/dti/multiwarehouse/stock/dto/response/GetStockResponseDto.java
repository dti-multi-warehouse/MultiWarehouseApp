package com.dti.multiwarehouse.stock.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GetStockResponseDto {
    private Long id;
    private Long warehouseId;
    private String thumbnail;
    private String name;
    private int stock;
}
