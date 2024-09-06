package com.dti.multiwarehouse.stock.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RequestMutationRequestDto {
    private Long productId;
    private Long warehouseToId;
    private Long warehouseFromId;
    private int quantity;
}
