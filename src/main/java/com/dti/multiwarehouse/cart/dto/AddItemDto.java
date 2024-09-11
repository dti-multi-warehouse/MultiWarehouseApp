package com.dti.multiwarehouse.cart.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AddItemDto {
    private Long productId;
    private Integer quantity;
}
