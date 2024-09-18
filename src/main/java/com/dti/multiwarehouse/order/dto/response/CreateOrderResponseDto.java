package com.dti.multiwarehouse.order.dto.response;

import com.dti.multiwarehouse.order.dao.enums.BankTransfer;
import com.dti.multiwarehouse.order.dao.enums.PaymentMethod;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateOrderResponseDto {
    private int price;
    private String accountNumber;
    private BankTransfer bankTransfer;
    private PaymentMethod paymentMethod;
    private Instant paymentExpiredAt;
}
