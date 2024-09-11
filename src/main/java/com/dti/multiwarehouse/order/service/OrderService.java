package com.dti.multiwarehouse.order.service;

import com.dti.multiwarehouse.order.dto.request.CreateOrderRequestDto;
import com.dti.multiwarehouse.order.dto.response.CreateOrderResponseDto;
import com.midtrans.httpclient.error.MidtransError;
import org.json.JSONObject;
import org.springframework.web.multipart.MultipartFile;

public interface OrderService {
    CreateOrderResponseDto createOrder(String sessionId, CreateOrderRequestDto requestDto) throws MidtransError;
    void uploadPaymentProof(Long id, MultipartFile image);
    void cancelOrder(Long id);
    void confirmPayment(Long id);
    void sendOrder(Long id);
    void finalizeOrder(Long id);
}
