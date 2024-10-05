package com.dti.multiwarehouse.order.dto.response;

import com.dti.multiwarehouse.order.dao.Order;
import com.dti.multiwarehouse.order.dao.enums.BankTransfer;
import com.dti.multiwarehouse.order.dao.enums.PaymentMethod;
import lombok.*;

import java.time.Instant;

@Getter
public class CreateOrderResponseDto {
    private final int price;
    private final String accountNumber;
    private final BankTransfer bankTransfer;
    private final PaymentMethod paymentMethod;
    private final Instant paymentExpiredAt;
    private final int shippingCost;

    public CreateOrderResponseDto(Order order) {
        this.price = order.getPrice();
        this.accountNumber = order.getAccountNumber();
        this.bankTransfer = order.getBank();
        this.paymentMethod = order.getPaymentMethod();
        this.paymentExpiredAt = order.getPaymentExpiredAt();
        this.shippingCost = order.getShippingCost();
    }
}
