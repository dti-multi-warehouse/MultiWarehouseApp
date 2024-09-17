package com.dti.multiwarehouse.order.dto.request;

import com.dti.multiwarehouse.order.dto.request.enums.BankTransfer;
import com.dti.multiwarehouse.order.dao.enums.PaymentMethod;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateOrderRequestDto {
    private PaymentMethod paymentMethod;
    private BankTransfer bankTransfer;
}