package com.dti.multiwarehouse.order.service.impl;

import com.dti.multiwarehouse.cart.dto.GetCartResponseDto;
import com.dti.multiwarehouse.cart.service.CartService;
import com.dti.multiwarehouse.cloudImageStorage.service.CloudImageStorageService;
import com.dti.multiwarehouse.exceptions.ApplicationException;
import com.dti.multiwarehouse.exceptions.InsufficientStockException;
import com.dti.multiwarehouse.order.dao.Order;
import com.dti.multiwarehouse.order.dao.OrderItem;
import com.dti.multiwarehouse.order.dao.enums.OrderStatus;
import com.dti.multiwarehouse.order.dto.request.CreateOrderRequestDto;
import com.dti.multiwarehouse.order.dao.enums.PaymentMethod;
import com.dti.multiwarehouse.order.dto.request.enums.BankTransfer;
import com.dti.multiwarehouse.order.dto.response.CreateOrderResponseDto;
import com.dti.multiwarehouse.order.helper.OrderMapper;
import com.dti.multiwarehouse.order.repository.OrderRepository;
import com.dti.multiwarehouse.order.service.OrderService;
import com.dti.multiwarehouse.product.service.ProductService;
import com.dti.multiwarehouse.stock.service.StockService;
import com.dti.multiwarehouse.user.entity.User;
import com.dti.multiwarehouse.user.service.UserService;
import com.dti.multiwarehouse.warehouse.dao.Warehouse;
import com.dti.multiwarehouse.warehouse.service.WarehouseService;
import com.midtrans.httpclient.error.MidtransError;
import com.midtrans.service.MidtransCoreApi;
import jakarta.annotation.Resource;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
    private final ProductService productService;

    @Resource
    private final MidtransCoreApi midtransCoreApi;

    @Transactional
    @Override
    public CreateOrderResponseDto createOrder(String sessionId, String email, CreateOrderRequestDto requestDto) throws MidtransError {
        var cart = cartService.getCart(sessionId);
        if (cart.getCartItems().isEmpty()) {
            throw new EntityNotFoundException("Cart is empty");
        }
        var user = userService.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        var warehouse = warehouseService.findWarehouseById(1L);
        var price = cart.getTotalPrice(); // + shipping fees
        var order = createNewOrder(user, warehouse, price, requestDto.getPaymentMethod(), cart);

        orderRepository.save(order);
        stockService.processOrder(warehouse.getId(), cart.getCartItems());
        cartService.deleteCart(sessionId);

//        if (requestDto.getPaymentMethod() == PaymentMethod.MIDTRANS) {
//            return processMidtransPayment(price, requestDto.getBankTransfer());
//        }
        order.getOrderItems().forEach(orderItem -> {
            productService.updateSold(orderItem.getProduct().getId());
        });
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
        var res = updateOrderStatus(
                id,
                OrderStatus.CANCELLED,
                null,
                List.of(OrderStatus.DELIVERING, OrderStatus.COMPLETED),
                "Order can no longer be cancelled at this stage"
        );
        res.getOrderItems().forEach(orderItem -> {
            productService.updateSold(orderItem.getProduct().getId());
        });
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

    private Order createNewOrder(User user, Warehouse warehouse, int price, PaymentMethod paymentMethod, GetCartResponseDto cart) {
        var order = Order.builder()
                .user(user)
                .warehouse(warehouse)
                .status(OrderStatus.AWAITING_CONFIRMATION)
                .paymentMethod(paymentMethod)
                .price(price)
                .orderItems(new HashSet<>())
                .build();

        for (var item : cart.getCartItems()) {
            if (item.getStock() < item.getQuantity()) {
                throw new InsufficientStockException("Insufficient stock for: " + item.getName());
            }
            var product = productService.findProductById(item.getProductId());
            var orderItem = new OrderItem(product, item.getQuantity());
            order.addOrderItem(orderItem);
        }
        return order;
    }

    private CreateOrderResponseDto processMidtransPayment(int price, BankTransfer bankTransfer) throws MidtransError {
        var idRand = UUID.randomUUID();

        Map<String, Object> params = new HashMap<>();
        params.put("payment_type", "bank_transfer");
        params.put("transaction_details", Map.of(
                "order_id", idRand.toString(),
                "gross_amount", price
        ));
        params.put("bank_transfer", Map.of(
                "bank", Optional.ofNullable(bankTransfer)
                        .map(bt -> bt.name().toLowerCase())
                        .orElse("bca")
        ));
        return OrderMapper.toCreateOrderResponseDto(midtransCoreApi.chargeTransaction(params));
    }

    private Order updateOrderStatus(Long id, OrderStatus status, OrderStatus expectedStatus, List<OrderStatus> invalidStatuses, String errorMessage) {
        var order = orderRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Order with id " + id + " not found"));
        if (invalidStatuses != null && invalidStatuses.contains(order.getStatus())) {
            throw new ApplicationException(errorMessage);
        }

        if (expectedStatus != null && order.getStatus() != expectedStatus) {
            throw new ApplicationException(errorMessage);
        }
        order.setStatus(status);
        return orderRepository.save(order);
    }
}
