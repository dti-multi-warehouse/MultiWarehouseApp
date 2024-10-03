package com.dti.multiwarehouse.order.dto.response;

import lombok.Getter;
import org.json.JSONObject;

@Getter
public class MidtransChargeDto {
    private final String transactionId;
    private final String currency;
    private final String price;
    private final String transactionTime;
    private final String transactionStatus;
    private final String paymentType;
    private final String bank;
    private final String vaNumber;
    private final String message;

    public MidtransChargeDto(JSONObject jsonObject) {
        var va = jsonObject.getJSONArray("va_numbers").getJSONObject(0);
        transactionId = jsonObject.getString("transaction_id");
        currency = jsonObject.getString("currency");
        price = jsonObject.getString("gross_amount");
        transactionTime = jsonObject.getString("transaction_time");
        transactionStatus = jsonObject.getString("transaction_status");
        paymentType = "Bank transfer";
        bank = va.getString("bank");
        vaNumber = va.getString("va_number");
        message = jsonObject.getString("status_message");
    }
}
