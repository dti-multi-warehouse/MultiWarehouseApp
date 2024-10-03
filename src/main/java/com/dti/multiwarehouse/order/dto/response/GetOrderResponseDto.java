package com.dti.multiwarehouse.order.dto.response;

import com.dti.multiwarehouse.order.dao.Order;
import lombok.Data;

import java.time.Instant;

@Data
public class GetOrderResponseDto {
    private Long id;
    private Long userId;
    private String userName;
    private Long warehouseId;
    private String warehouseName;
    private int price;
    private String paymentProof;
    private String status;
    private String paymentMethod;
    private int shippingCost;
    private String bank;
    private String accountNumber;
    private Instant createdAt;
    private Instant paymentExpiredAt;

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
       this.accountNumber = order.getAccountNumber();
       this.createdAt = order.getCreatedAt();
       this.paymentExpiredAt = order.getPaymentExpiredAt();
   }

}
