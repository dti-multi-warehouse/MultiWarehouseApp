package com.dti.multiwarehouse.stock.dto.response;

public interface RetrieveStock {
    Long getId();
    String getName();
    int getStock();
    String getThumbnail();
    int getIncoming();
    int getOutgoing();
}
