package com.dti.multiwarehouse.stock.dto.response;

import lombok.Getter;

import java.time.Instant;

@Getter
public class StockMovement {
    private final Instant date;
    private final int quantity;
    private final String source;
    private final int note;

    public StockMovement(RetrieveStockDetails dto) {
        this.date = dto.getCreatedAt();
        this.quantity = dto.getQuantity();
        this.source = dto.getSource();
        this.note = dto.getNote();
    }
}
