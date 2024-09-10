package com.dti.multiwarehouse.order.service.impl;

import com.dti.multiwarehouse.cart.service.CartService;
import com.dti.multiwarehouse.exceptions.InsufficientStockException;
import com.dti.multiwarehouse.order.dao.Order;
import com.dti.multiwarehouse.order.dao.enums.OrderStatus;
import com.dti.multiwarehouse.order.dto.request.CreateOrderRequestDto;
import com.dti.multiwarehouse.order.dto.response.CreateOrderResponseDto;
import com.dti.multiwarehouse.order.repository.OrderRepository;
import com.dti.multiwarehouse.order.service.OrderService;
import com.dti.multiwarehouse.user.service.UserService;
import com.dti.multiwarehouse.warehouse.service.WarehouseService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;

    private final CartService cartService;
    private final UserService userService;
    private final WarehouseService warehouseService;

    @Override
    public CreateOrderResponseDto createOrder(String sessionId) {
        var cart = cartService.getCart(sessionId);
//        check whether the stock is sufficient or not
        cart.getCartItems().forEach(item -> {
            if (item.getStock() < item.getQuantity()) {
                throw new InsufficientStockException("Insufficient stock for: " + item.getName());
            }
        });
//        fetch user
//        find and fetch warehouse
        var warehouse = warehouseService.findWarehouseById(1L);
//        calculate price\
        var price = cart.getTotalPrice(); // + shipping fees
        var order = Order.builder()
                .user(null)
                .warehouse(warehouse)
                .status(OrderStatus.AWAITING_PAYMENT)
                .paymentProof(null)
                .price(price)
                .build();
        orderRepository.save(order);
        return null;
    }
}
