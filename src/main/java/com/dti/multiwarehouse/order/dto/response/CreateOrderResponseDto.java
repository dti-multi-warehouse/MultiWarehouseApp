package com.dti.multiwarehouse.order.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateOrderResponseDto {
    private String transactionId;
    private String currency;
    private String price;
    private String transactionTime;
    private String transactionStatus;
    private String paymentType;
    private String bank;
    private String vaNumber;
    private String message;
}
