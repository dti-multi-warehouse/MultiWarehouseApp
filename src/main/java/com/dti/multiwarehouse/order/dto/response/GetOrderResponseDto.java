package com.dti.multiwarehouse.order.dto.response;

import com.dti.multiwarehouse.order.dao.Order;
import lombok.Getter;

import java.time.Instant;

@Getter
public class GetOrderResponseDto {
    private final Long id;
    private final Long userId;
    private final String userName;
    private final Long warehouseId;
    private final String warehouseName;
    private final int price;
    private final String paymentProof;
    private final String status;
    private final String paymentMethod;
    private final int shippingCost;
    private final String bank;
    private final String accountNumber;
    private final Instant createdAt;
    private final Instant paymentExpiredAt;

   public GetOrderResponseDto(Order order) {
       this.id = order.getId();
       this.userId = order.getUser().getId();
       this.userName = order.getUser().getUsername();
       this.warehouseId = order.getWarehouse().getId();
       this.warehouseName = order.getWarehouse().getName();
       this.price = order.getPrice();
       this.paymentProof = order.getPaymentProof();
       this.status = order.getStatus().name();
       this.paymentMethod = order.getPaymentMethod().name();
       this.shippingCost = order.getShippingCost();
       this.bank = order.getBank().name();
       this.accountNumber = order.getAccountNumber();
       this.createdAt = order.getCreatedAt();
       this.paymentExpiredAt = order.getPaymentExpiredAt();
   }

}
