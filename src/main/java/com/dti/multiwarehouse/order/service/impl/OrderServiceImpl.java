package com.dti.multiwarehouse.order.service.impl;

import com.dti.multiwarehouse.cart.service.CartService;
import com.dti.multiwarehouse.cloudImageStorage.service.CloudImageStorageService;
import com.dti.multiwarehouse.exceptions.ApplicationException;
import com.dti.multiwarehouse.exceptions.InsufficientStockException;
import com.dti.multiwarehouse.order.dao.Order;
import com.dti.multiwarehouse.order.dao.enums.OrderStatus;
import com.dti.multiwarehouse.order.dto.request.CreateOrderRequestDto;
import com.dti.multiwarehouse.order.dto.request.enums.PaymentMethod;
import com.dti.multiwarehouse.order.dto.response.CreateOrderResponseDto;
import com.dti.multiwarehouse.order.helper.OrderMapper;
import com.dti.multiwarehouse.order.repository.OrderRepository;
import com.dti.multiwarehouse.order.service.OrderService;
import com.dti.multiwarehouse.stock.service.StockService;
import com.dti.multiwarehouse.user.service.UserService;
import com.dti.multiwarehouse.warehouse.service.WarehouseService;
import com.midtrans.httpclient.error.MidtransError;
import com.midtrans.service.MidtransCoreApi;
import jakarta.annotation.Resource;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

@RequiredArgsConstructor
@Service
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;

    private final StockService stockService;
    private final CartService cartService;
    private final UserService userService;
    private final WarehouseService warehouseService;
    private final CloudImageStorageService cloudImageStorageService;

    @Resource
    private final MidtransCoreApi midtransCoreApi;

    @Override
    public CreateOrderResponseDto createOrder(String sessionId, CreateOrderRequestDto requestDto) throws MidtransError {
        var cart = cartService.getCart(sessionId);
        if (cart.getCartItems().isEmpty()) {
            throw new EntityNotFoundException("Cart is empty");
        }
//        check whether the stock is sufficient or not
        cart.getCartItems().forEach(item -> {
            if (item.getStock() < item.getQuantity()) {
                throw new InsufficientStockException("Insufficient stock for: " + item.getName());
            }
        });
//        fetch user
        var userOptional = userService.findByEmail("reiss@mail.com");
        if (userOptional.isEmpty()) {
            throw new ApplicationException("User not found");
        }
//        find and fetch warehouse
        var warehouse = warehouseService.findWarehouseById(1L);
        var price = cart.getTotalPrice(); // + shipping fees
        var order = Order.builder()
                .user(userOptional.get())
                .warehouse(warehouse)
                .status(OrderStatus.AWAITING_PAYMENT)
                .paymentProof(null)
                .price(price)
                .build();
        var savedOrder = orderRepository.save(order);
        stockService.processOrder(warehouse.getId(), cart.getCartItems());
        cartService.deleteCart(sessionId);
        if (requestDto.getPaymentMethod() == PaymentMethod.MIDTRANS) {
            UUID idRand = UUID.randomUUID();

            Map<String, Object> params = new HashMap<>();
            Map<String, String> transactionDetails = new HashMap<>();
            transactionDetails.put("order_id", String.valueOf(idRand));
            transactionDetails.put("gross_amount", String.valueOf(price));

            Map<String, String> bankTransfer = new HashMap<>();
            if (requestDto.getBankTransfer() == null) {
                bankTransfer.put("bank", "bca");
            } else {
                bankTransfer.put("bank", requestDto.getBankTransfer().name().toLowerCase());
            }
            params.put("payment_type", "bank_transfer");
            params.put("transaction_details", transactionDetails);
            params.put("bank_transfer", bankTransfer);
            var res = midtransCoreApi.chargeTransaction(params);
            return OrderMapper.toCreateOrderResponseDto(res);
        }
        return null;
    }

    @Override
    public void uploadPaymentProof(Long id, MultipartFile image) {
        var order = orderRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Order with id " + id + " not found"));
        try {
           var url = cloudImageStorageService.uploadImage(image, "payment_proof");
           order.setPaymentProof(url);
           order.setStatus(OrderStatus.AWAITING_CONFIRMATION);
           orderRepository.save(order);
        } catch (IOException e) {
            throw new ApplicationException("Failed to upload image");
        }
    }

    @Override
    public void cancelOrder(Long id) {
        updateOrderStatus(
                id,
                OrderStatus.CANCELLED,
                null,
                List.of(OrderStatus.DELIVERING, OrderStatus.COMPLETED),
                "Order can no longer be cancelled at this stage"
        );
    }

    @Override
    public void confirmPayment(Long id) {
        updateOrderStatus(
                id,
                OrderStatus.PROCESSING,
                OrderStatus.AWAITING_CONFIRMATION,
                null,
                "Order can't be processed"
        );
    }

    @Override
    public void sendOrder(Long id) {
        updateOrderStatus(
                id,
                OrderStatus.DELIVERING,
                OrderStatus.PROCESSING,
                null,
                "Order can't be delivered"
        );
    }

    @Override
    public void finalizeOrder(Long id) {
        updateOrderStatus(
                id,
                OrderStatus.COMPLETED,
                OrderStatus.DELIVERING,
                null,
                "Order can't be completed"
        );
    }

    private void updateOrderStatus(Long id, OrderStatus status, OrderStatus expectedStatus, List<OrderStatus> invalidStatuses, String errorMessage) {
        var order = orderRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Order with id " + id + " not found"));
        if (invalidStatuses != null && invalidStatuses.contains(order.getStatus())) {
            throw new ApplicationException(errorMessage);
        }

        if (expectedStatus != null && order.getStatus() != expectedStatus) {
            throw new ApplicationException(errorMessage);
        }
        order.setStatus(status);
        orderRepository.save(order);
    }
}
