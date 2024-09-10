package com.dti.multiwarehouse.order.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateOrderRequestDto {
    @NotNull
    @Valid
    private Set<CreateOrderItemRequestDto> items;
}
