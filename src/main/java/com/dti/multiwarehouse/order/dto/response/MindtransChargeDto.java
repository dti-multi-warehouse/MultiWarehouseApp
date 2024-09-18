package com.dti.multiwarehouse.order.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.json.JSONObject;

@Data
public class MindtransChargeDto {
    private String transactionId;
    private String currency;
    private String price;
    private String transactionTime;
    private String transactionStatus;
    private String paymentType;
    private String bank;
    private String vaNumber;
    private String message;

    public MindtransChargeDto(JSONObject jsonObject) {
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
