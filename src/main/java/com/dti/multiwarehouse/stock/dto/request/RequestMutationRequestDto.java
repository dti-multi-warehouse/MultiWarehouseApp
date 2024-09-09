package com.dti.multiwarehouse.stock.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RequestMutationRequestDto {
    @NotNull
    @Positive
    private Long productId;
    @NotNull
    @Positive
    private Long warehouseToId;
    @NotNull
    @Positive
    private Long warehouseFromId;
    @NotNull
    @Positive
    private int quantity;
}
