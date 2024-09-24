package com.dti.multiwarehouse.order.dto.request;

import com.dti.multiwarehouse.order.dao.enums.BankTransfer;
import com.dti.multiwarehouse.order.dao.enums.PaymentMethod;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateOrderRequestDto {
    private PaymentMethod paymentMethod;
    private BankTransfer bankTransfer;
    private Long shippingAddressId;
    private Long warehouseId;
    private List<Long> productIds;
    private String shippingMethod;
}
