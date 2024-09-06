package com.dti.multiwarehouse.stock.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RestockRequestDto {
    private Long productId;
    private Long warehouseToId;
    private int quantity;
}
