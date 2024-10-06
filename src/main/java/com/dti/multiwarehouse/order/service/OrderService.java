package com.dti.multiwarehouse.order.service;

import com.dti.multiwarehouse.order.dto.request.CreateOrderRequestDto;
import com.dti.multiwarehouse.order.dto.response.CreateOrderResponseDto;
import com.dti.multiwarehouse.order.dto.response.GetOrderResponseDto;
import com.midtrans.httpclient.error.MidtransError;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface OrderService {
    CreateOrderResponseDto createOrder(String sessionId, String email, CreateOrderRequestDto requestDto) throws MidtransError;
    List<GetOrderResponseDto> getAdminOrders(Long warehouseId);
    List<GetOrderResponseDto> getUserOrders(Long userId);
    void uploadPaymentProof(Long id, MultipartFile image);
    void cancelOrder(Long id);
    void confirmPayment(Long id);
    void sendOrder(Long id);
    void finalizeOrder(Long id);
}
