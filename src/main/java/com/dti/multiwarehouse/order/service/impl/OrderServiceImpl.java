package com.dti.multiwarehouse.order.service.impl;

import com.dti.multiwarehouse.address.entity.UserAddress;
import com.dti.multiwarehouse.address.repository.UserAddressRepository;
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
import com.dti.multiwarehouse.order.dao.enums.BankTransfer;
import com.dti.multiwarehouse.order.dto.request.ShippingCostRequestDto;
import com.dti.multiwarehouse.order.dto.response.*;
import com.dti.multiwarehouse.order.repository.OrderRepository;
import com.dti.multiwarehouse.order.service.OrderService;
import com.dti.multiwarehouse.order.service.ShippingService;
import com.dti.multiwarehouse.product.dao.Product;
import com.dti.multiwarehouse.product.service.ProductService;
import com.dti.multiwarehouse.stock.service.StockService;
import com.dti.multiwarehouse.user.entity.User;
import com.dti.multiwarehouse.user.service.UserService;
import com.dti.multiwarehouse.warehouse.dao.Warehouse;
import com.midtrans.httpclient.error.MidtransError;
import com.midtrans.service.MidtransCoreApi;
import jakarta.annotation.Resource;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    @Lazy
    private final StockService stockService;
    private final CartService cartService;
    private final UserService userService;
    private final CloudImageStorageService cloudImageStorageService;
    private final ProductService productService;
    private final ShippingService shippingService;
    private final UserAddressRepository userAddressRepository;

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

        UserAddress userAddress = userAddressRepository.findById(requestDto.getShippingAddressId())
                .orElseThrow(() -> new EntityNotFoundException("Shipping address not found"));

        Warehouse nearestWarehouse = shippingService.findNearestWarehouse(userAddress.getId());
        if (nearestWarehouse == null) {
            throw new EntityNotFoundException("No suitable warehouse found for the shipping address");
        }

        checkStockAvailability(requestDto.getProductIds(), nearestWarehouse);
        int totalWeight = calculateTotalWeight(requestDto.getProductIds());

        ShippingCostRequestDto shippingRequest = new ShippingCostRequestDto();
        shippingRequest.setOriginCityId(nearestWarehouse.getId());
        shippingRequest.setDestinationCityId(userAddress.getId());
        shippingRequest.setWeight(totalWeight);
        shippingRequest.setCourier(requestDto.getShippingMethod());

        ShippingCostResponseDto shippingResponse = shippingService.calculateShippingCost(shippingRequest);
        if (shippingResponse.getRajaOngkir() != null && !shippingResponse.getRajaOngkir().getResults().isEmpty()) {
            var result = shippingResponse.getRajaOngkir().getResults().get(0);
            List<ShippingCostResponseDto.RajaOngkir.Result.Cost> costs = result.getCosts();
            if (costs.isEmpty() || costs.get(0).getCost().isEmpty()) {
                throw new ApplicationException(HttpStatus.BAD_REQUEST, "No shipping cost available for the selected method.");
            }

            int shippingCost = costs.get(0).getCost().get(0).getValue();
            var totalPrice = cart.getTotalPrice() + shippingCost;

            var order = createNewOrder(user, nearestWarehouse, totalPrice, requestDto.getPaymentMethod(), cart, userAddress);
            order.setShippingCost(shippingCost);

            if (requestDto.getPaymentMethod() == PaymentMethod.MIDTRANS) {
                var res = processMidtransPayment(totalPrice, requestDto.getBankTransfer());
                order.setBank(BankTransfer.valueOf(res.getBank().toUpperCase()));
                order.setAccountNumber(res.getVaNumber());
                order.setMidtransId(res.getTransactionId());
            } else {
                order.setBank(BankTransfer.BCA);
                order.setAccountNumber(UUID.randomUUID().toString());
            }

            orderRepository.save(order);
            stockService.processOrder(nearestWarehouse.getId(), cart.getCartItems());
            cartService.deleteCart(sessionId);

            return new CreateOrderResponseDto(order);
        } else {
            throw new ApplicationException(HttpStatus.BAD_REQUEST, "No shipping options available.");
        }
    }

    private int calculateTotalWeight(List<Long> productIds) {
        int totalWeight = 0;
        int defaultWeightPerProduct = 300;
        for (Long productId : productIds) {
            totalWeight += defaultWeightPerProduct;
        }

        return totalWeight;
    }

    private void checkStockAvailability(List<Long> productIds, Warehouse nearestWarehouse) {
        for (Long productId : productIds) {
            Product product = productService.findProductById(productId);
            if (product.getStock() <= 0) {
                throw new InsufficientStockException("Insufficient stock for product: " + product.getName());
            }
        }
    }

    @Override
    public GetOrderResponseDto getAdminOrders(Long warehouseId, int page) {
        var res = orderRepository.findAllByWarehouseIdOrderByCreatedAtDesc(warehouseId, PageRequest.of(page, 10));
        var orders = res.getContent()
                .stream()
                .map(OrderResponseDto::new)
                .toList();
        return new GetOrderResponseDto(res.getTotalPages(), orders);
    }

    @Override
    public GetOrderResponseDto getUserOrders(Long userId, int page) {
        var res = orderRepository.findAllByUserIdOrderByCreatedAtDesc(userId, PageRequest.of(page, 10));
        var orders = res.getContent()
                .stream()
                .map(OrderResponseDto::new)
                .toList();
        return new GetOrderResponseDto(res.getTotalPages(), orders);
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
        var order = orderRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Order with id " + id + " not found"));

        if (order.getStatus() == OrderStatus.DELIVERING || order.getStatus() == OrderStatus.COMPLETED) {
           throw new ApplicationException("Order can no longer be cancelled at this stage");
        }

        order.setStatus(OrderStatus.CANCELLED);
        var res = orderRepository.save(order);
        res.getOrderItems().forEach(orderItem -> productService.updateSoldAndStock(orderItem.getProduct().getId()));
    }

    @Override
    public void confirmPayment(Long id) {
        var order = orderRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Order with id " + id + " not found"));

        if (order.getStatus() != OrderStatus.AWAITING_CONFIRMATION) {
            throw new ApplicationException("Order can't be processed");
        }

        order.setStatus(OrderStatus.PROCESSING);
        orderRepository.save(order);
    }

    @Override
    public void sendOrder(Long id) {
        var order = orderRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Order with id " + id + " not found"));

        if (order.getStatus() != OrderStatus.PROCESSING) {
            throw new ApplicationException("Order can't be delivered");
        }

        order.setStatus(OrderStatus.DELIVERING);
        orderRepository.save(order);
    }

    @Override
    public void finalizeOrder(Long id) {
        var order = orderRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Order with id " + id + " not found"));

        if (order.getStatus() != OrderStatus.DELIVERING) {
            throw new ApplicationException("Order can't be completed");
        }

        order.setStatus(OrderStatus.COMPLETED);
        orderRepository.save(order);
    }

    @Override
    public void handlePaymentNotification(String midtransId) {
        var order = orderRepository.findByMidtransId(midtransId).orElseThrow(() -> new EntityNotFoundException("Order with id " + midtransId + " not found"));
        order.setStatus(OrderStatus.PROCESSING);
        orderRepository.save(order);
    }

    private Order createNewOrder(User user, Warehouse warehouse, int price, PaymentMethod paymentMethod, GetCartResponseDto cart, UserAddress shippingAddress) {
        var order = Order.builder()
                .user(user)
                .warehouse(warehouse)
                .shippingAddress(shippingAddress)
                .price(price)
                .status(OrderStatus.AWAITING_PAYMENT)
                .paymentMethod(paymentMethod)
                .orderItems(new ArrayList<>())
                .paymentExpiredAt(Instant.now().plus(1, ChronoUnit.DAYS))
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

    private MidtransChargeDto processMidtransPayment(int price, BankTransfer bankTransfer) throws MidtransError {
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
        return new MidtransChargeDto(midtransCoreApi.chargeTransaction(params));
    }

    @Transactional(readOnly = true)
    @Override
    public List<OrderDetailsResponseDto> getUserOrdersByStatus(Long userId, String status) {
        OrderStatus orderStatus;
        try {
            orderStatus = OrderStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ApplicationException(HttpStatus.BAD_REQUEST, "Invalid order status: " + status);
        }

        List<Order> orders = orderRepository.findAllByUserIdAndStatus(userId, orderStatus);
        if (orders.isEmpty()) {
            throw new EntityNotFoundException("No orders found for user with status: " + status);
        }

        return orders.stream()
                .map(OrderDetailsResponseDto::new)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<OrderDetailsResponseDto> getOrderDetailsById(Long orderId) {
        return orderRepository.findById(orderId).map(OrderDetailsResponseDto::new);
    }
}
