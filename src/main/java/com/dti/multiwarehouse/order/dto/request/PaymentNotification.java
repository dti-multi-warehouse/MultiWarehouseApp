package com.dti.multiwarehouse.order.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentNotification {
    private String transactionStatus;
    private String transactionId;
    private String fraudStatus;
}
