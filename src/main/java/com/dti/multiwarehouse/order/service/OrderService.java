package com.dti.multiwarehouse.order.service;

import com.dti.multiwarehouse.order.dto.request.CreateOrderRequestDto;
import com.dti.multiwarehouse.order.dto.response.CreateOrderResponseDto;
import com.dti.multiwarehouse.order.dto.response.GetOrderResponseDto;
import com.dti.multiwarehouse.order.dto.response.OrderDetailsResponseDto;
import com.midtrans.httpclient.error.MidtransError;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

public interface OrderService {
    CreateOrderResponseDto createOrder(String sessionId, String email, CreateOrderRequestDto requestDto) throws MidtransError;
    GetOrderResponseDto getAdminOrders(Long warehouseId, int page);
    GetOrderResponseDto getUserOrders(Long userId, int page);
    void uploadPaymentProof(Long id, MultipartFile image, Long userId);
    void cancelOrder(Long id, Long userId, Long warehouseId, boolean isUser, boolean isAdmin);
    void confirmPayment(Long id, Long warehouseId, boolean isAdmin);
    void sendOrder(Long id, Long warehouseId, boolean isAdmin);
    void finalizeOrder(Long id, Long userId);
    void handlePaymentNotification(String midtransId);
    List<OrderDetailsResponseDto> getUserOrdersByStatus(Long userId, String status);
    Optional<OrderDetailsResponseDto> getOrderDetailsById(Long orderId);
}
