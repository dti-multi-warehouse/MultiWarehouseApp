package com.dti.multiwarehouse.dashboard.dto.response;

import java.time.Instant;

public interface RetrieveProductStockDetails {
    Long getId();
    int getQuantity();
    Instant getDate();
}
