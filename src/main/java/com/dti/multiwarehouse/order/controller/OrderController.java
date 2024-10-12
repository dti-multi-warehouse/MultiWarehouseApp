package com.dti.multiwarehouse.order.controller;

import com.dti.multiwarehouse.order.dto.request.CreateOrderRequestDto;
import com.dti.multiwarehouse.order.dto.request.PaymentNotification;
import com.dti.multiwarehouse.order.dto.request.ShippingCostRequestDto;
import com.dti.multiwarehouse.order.dto.response.ShippingCostResponseDto;
import com.dti.multiwarehouse.order.service.OrderService;
import com.dti.multiwarehouse.order.service.ShippingService;
import com.dti.multiwarehouse.response.Response;
import com.midtrans.httpclient.error.MidtransError;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Objects;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/order")
public class OrderController {
    private final OrderService orderService;
    private final ShippingService shippingService;

    @GetMapping("/user/{id}")
    public ResponseEntity<?> getUserOrder(
            @PathVariable("id") Long id,
            @RequestParam(defaultValue = "0") int page
    ) {
        var res = orderService.getUserOrders(id, page);
        return Response.success("Successfully retrieved order", res);
    }

    @GetMapping("/admin/{id}")
    @PreAuthorize("hasAnyRole('admin', 'warehouse_admin')")
    public ResponseEntity<?> getAdminOrder(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable("id") Long id,
            @RequestParam(defaultValue = "0") int page
            ) {
        if (jwt == null || jwt.getClaim("warehouse_id") == null || !jwt.getClaim("warehouse_id").equals(id)) {
            return Response.failed("Invalid authority");
        }
        var res = orderService.getAdminOrders(id, page);
        return Response.success("Successfully retrieved order", res);
    }

    @PostMapping("/shipping-cost")
    public ResponseEntity<Response<ShippingCostResponseDto>> calculateShippingCost(@RequestBody ShippingCostRequestDto request) {
        ShippingCostResponseDto response = shippingService.calculateShippingCost(request);

        if (response.getRajaOngkir().getResults().isEmpty() || response.getRajaOngkir().getResults().get(0).getCosts().isEmpty()) {
            return Response.success(HttpStatus.OK.value(), "No shipping costs available for the given parameters", response);
        }

        return Response.success(HttpStatus.OK.value(), "Shipping costs calculated successfully", response);
    }

    @PostMapping
    public ResponseEntity<?> createOrder(@AuthenticationPrincipal Jwt jwt, @RequestBody CreateOrderRequestDto requestDto) throws MidtransError {
        var res = orderService.createOrder(jwt.getTokenValue(), jwt.getSubject(), requestDto);
        return Response.success("Successfully placed order", res);
    }

    @PostMapping("/{id}/payment")
    public ResponseEntity<?> createPayment(@PathVariable("id") Long id, MultipartFile paymentProof) {
        orderService.uploadPaymentProof(id, paymentProof);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<?> cancelOrder(@PathVariable("id") Long id) {
        orderService.cancelOrder(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}/confirm")
    @PreAuthorize("hasAnyRole('admin', 'warehouse_admin')")
    public ResponseEntity<?> confirmPayment(@PathVariable("id") Long id) {
        orderService.confirmPayment(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}/send")
    @PreAuthorize("hasAnyRole('admin', 'warehouse_admin')")
    public ResponseEntity<?> sendOrder(@PathVariable("id") Long id) {
        orderService.sendOrder(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}/finalize")
    public ResponseEntity<?> finalizeOrder(@PathVariable("id") Long id) {
        orderService.finalizeOrder(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/user/{id}/status/{status}")
    public ResponseEntity<?> getUserOrderByStatus(@PathVariable("id") Long userId, @PathVariable("status") String status) {
        var orders = orderService.getUserOrdersByStatus(userId, status);
        return Response.success("Successfully retrieved order", orders);
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<?> getOrderDetails(@PathVariable("orderId") Long orderId) {
        var orderDetails = orderService.getOrderDetailsById(orderId);
        return Response.success("Successfully retrieved order details", orderDetails);
    }

    @PostMapping("/notification")
    public void handlePaymentNotification(@RequestBody PaymentNotification paymentNotification) {
        if (!Objects.equals(paymentNotification.getFraudStatus(), "accept")){
            return;
        }
        if (Objects.equals(paymentNotification.getTransactionStatus(), "settlement") ||
                Objects.equals(paymentNotification.getTransactionStatus(), "capture")
        ) {
            orderService.handlePaymentNotification(paymentNotification.getTransactionId());
        }

    }
}
