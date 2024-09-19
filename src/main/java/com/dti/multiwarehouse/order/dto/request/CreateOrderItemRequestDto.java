package com.dti.multiwarehouse.order.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateOrderItemRequestDto {
    @NotNull
    @Positive
    private Long productId;

    @NotNull
    @Positive
    private int quantity;
}
