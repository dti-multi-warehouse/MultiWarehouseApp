package com.dti.multiwarehouse.order.service;

import com.dti.multiwarehouse.order.dto.request.CreateOrderRequestDto;
import com.dti.multiwarehouse.order.dto.response.CreateOrderResponseDto;

public interface OrderService {
    CreateOrderResponseDto createOrder(String sessionId);
}
