package com.dti.multiwarehouse.order.service.impl;

import com.dti.multiwarehouse.order.dto.request.CreateOrderRequestDto;
import com.dti.multiwarehouse.order.dto.response.CreateOrderResponseDto;
import com.dti.multiwarehouse.order.repository.OrderRepository;
import com.dti.multiwarehouse.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;

    @Override
    public CreateOrderResponseDto createOrder(Long userId, CreateOrderRequestDto createOrderRequestDto) {
//        fetch user
//        find and fetch warehouse
//        calculate price
//
        return null;
    }
}
