package com.dti.multiwarehouse.order.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentNotification {
    @JsonProperty("transaction_status")
    private String transactionStatus;
    @JsonProperty("transaction_id")
    private String transactionId;
    @JsonProperty("fraud_status")
    private String fraudStatus;
}
