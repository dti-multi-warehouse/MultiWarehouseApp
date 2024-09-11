package com.dti.multiwarehouse.order.controller;

import com.dti.multiwarehouse.order.dto.request.CreateOrderRequestDto;
import com.dti.multiwarehouse.order.service.OrderService;
import com.dti.multiwarehouse.response.Response;
import com.midtrans.httpclient.error.MidtransError;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/order")
public class OrderController {
    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<?> createOrder(@RequestBody CreateOrderRequestDto requestDto) throws MidtransError {
        var res = orderService.createOrder("ehehehe", requestDto);
        return Response.success("Successfully placed order", res);
    }

    @PostMapping("/payment/{id}")
    public ResponseEntity<?> createPayment(@PathVariable("id") Long id, MultipartFile paymentProof) {
        orderService.uploadPaymentProof(id,paymentProof);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/cancel/{id}")
    public ResponseEntity<?> cancelOrder(@PathVariable("id") Long id) {
        orderService.cancelOrder(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/confirm/{id}")
    public ResponseEntity<?> confirmPayment(@PathVariable("id") Long id) {
        orderService.confirmPayment(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/send/{id}")
    public ResponseEntity<?> sendOrder(@PathVariable("id") Long id) {
        orderService.sendOrder(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/finalize/{id}")
    public ResponseEntity<?> finalizeOrder(@PathVariable("id") Long id) {
        orderService.finalizeOrder(id);
        return ResponseEntity.ok().build();
    }

}
