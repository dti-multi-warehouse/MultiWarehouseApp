package com.dti.multiwarehouse.stock.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetWarehouseAndStockAvailabililtyRequestDto {
    private Long warehouseId;
    private Long productId;
}
