package com.dti.multiwarehouse.order.helper;

import com.dti.multiwarehouse.order.dto.response.CreateOrderResponseDto;
import org.json.JSONObject;

public class OrderMapper {

    public static CreateOrderResponseDto toCreateOrderResponseDto(JSONObject jsonObject) {
        var vaNumber = jsonObject.getJSONArray("va_numbers").getJSONObject(0);
        return CreateOrderResponseDto.builder()
                .transactionId(jsonObject.getString("transaction_id"))
                .currency(jsonObject.getString("currency"))
                .price(jsonObject.getString("gross_amount"))
                .transactionTime(jsonObject.getString("transaction_time"))
                .transactionStatus(jsonObject.getString("transaction_status"))
                .paymentType("Bank transfer")
                .bank(vaNumber.getString("bank"))
                .vaNumber(vaNumber.getString("va_number"))
                .message(jsonObject.getString("status_message"))
                .build();
    }
}
