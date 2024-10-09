package com.dti.multiwarehouse.order.dto.response;

import java.util.List;

public record GetOrderResponseDto(int totalPages, List<OrderResponseDto> orders) {
}
