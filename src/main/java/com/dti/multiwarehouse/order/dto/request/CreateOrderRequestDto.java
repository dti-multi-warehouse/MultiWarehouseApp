package com.dti.multiwarehouse.order.dto.request;

import com.dti.multiwarehouse.order.dto.request.enums.BankTransfer;
import com.dti.multiwarehouse.order.dto.request.enums.PaymentMethod;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateOrderRequestDto {
    private PaymentMethod paymentMethod;
    private BankTransfer bankTransfer;
}
