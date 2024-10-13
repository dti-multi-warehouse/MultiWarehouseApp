package com.dti.multiwarehouse.stock.dto.response;

public interface RetrieveWarehouseAndStockAvailabilityDto {
    Long getWarehouseId();
    String getWarehouseName();
    int getStock();
}
