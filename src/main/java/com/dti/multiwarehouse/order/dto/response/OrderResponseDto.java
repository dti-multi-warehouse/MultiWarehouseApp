package com.dti.multiwarehouse.order.dto.response;

import com.dti.multiwarehouse.address.entity.UserAddress;
import com.dti.multiwarehouse.order.dao.Order;
import lombok.Getter;

import java.time.Instant;
import java.util.List;

@Getter
public class OrderResponseDto {
    private final Long id;
    private final Long userId;
    private final String userName;
    private final String email;
    private final String phoneNumber;
    private final String street;
    private final String city;
    private final String province;
    private final int price;
    private final String paymentProof;
    private final String status;
    private final String paymentMethod;
    private final int shippingCost;
    private final String bank;
    private final String accountNumber;
    private final Instant createdAt;
    private final Instant paymentExpiredAt;
    private final List<OrderItemDto> orderItems;

   public OrderResponseDto(Order order) {
       var items = order.getOrderItems().stream().map(OrderItemDto::new).toList();
       this.id = order.getId();
       this.userId = order.getUser().getId();
       this.userName = order.getUser().getUsername();
       this.email = order.getUser().getEmail();
       this.phoneNumber = order.getShippingAddress().getPhoneNumber();
       this.street = order.getShippingAddress().getAddress().getStreet();
       this.city = order.getShippingAddress().getAddress().getCity();
       this.province = order.getShippingAddress().getAddress().getProvince();
       this.price = order.getPrice();
       this.paymentProof = order.getPaymentProof();
       this.status = order.getStatus().name();
       this.paymentMethod = order.getPaymentMethod().name();
       this.shippingCost = order.getShippingCost();
       this.bank = order.getBank().name();
       this.accountNumber = order.getAccountNumber();
       this.createdAt = order.getCreatedAt();
       this.paymentExpiredAt = order.getPaymentExpiredAt();
       this.orderItems = items;
   }

}
