package com.dti.multiwarehouse.order.dto.response;

import java.util.List;

public record GetOrderResponseDto(int totalPage, List<OrderResponseDto> orders) {
}
