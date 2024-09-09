package com.dti.multiwarehouse.order.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.Set;

public class CreateOrderRequestDto {
    @NotNull
    @Valid
    private Set<CreateOrderItemRequestDto> items;
}
