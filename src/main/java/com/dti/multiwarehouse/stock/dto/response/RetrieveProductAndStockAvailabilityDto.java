package com.dti.multiwarehouse.stock.dto.response;

public interface RetrieveProductAndStockAvailabilityDto {
    Long getId();
    String getName();
    int getStock();
    String getThumbnail();
}
