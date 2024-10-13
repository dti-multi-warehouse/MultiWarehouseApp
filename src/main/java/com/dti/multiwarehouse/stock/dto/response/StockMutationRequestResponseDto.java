package com.dti.multiwarehouse.stock.dto.response;

import com.dti.multiwarehouse.stock.dao.StockMutation;
import lombok.Getter;

import java.time.Instant;

@Getter
public class StockMutationRequestResponseDto {
    public final Long id;
    public final String warehouseToName;
    public final int quantity;
    public final String name;
    public final Instant createdAt;

    public StockMutationRequestResponseDto(StockMutation stockMutation) {
        this.id = stockMutation.getId();
        this.warehouseToName = stockMutation.getWarehouseTo().getName();
        this.quantity = stockMutation.getQuantity();
        this.name = stockMutation.getProduct().getName();
        this.createdAt = stockMutation.getCreatedAt();
    }
}
