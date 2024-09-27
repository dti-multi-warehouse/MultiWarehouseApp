package com.dti.multiwarehouse.dashboard.dto.response;

public interface RetrieveMonthlyStockSummary {
    Long getId();
    String getName();
    int getIncoming();
    int getOutgoing();
    int getStock();
}
