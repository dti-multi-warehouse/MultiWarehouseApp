package com.dti.multiwarehouse.stock.dto.response;

import java.time.Instant;

public interface RetrieveStockDetails {
    Instant getCreatedAt();
    int getQuantity();
    String getSource();
    String getNote();
}
