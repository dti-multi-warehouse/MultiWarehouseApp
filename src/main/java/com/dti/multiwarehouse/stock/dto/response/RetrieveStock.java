package com.dti.multiwarehouse.stock.dto.response;

import java.time.Instant;

public interface RetrieveStock {
    Long getId();
    String getName();
    int getStock();
    String getThumbnail();
    int getIncoming();
    int getOutgoing();
    Instant getDeletedAt();
}
