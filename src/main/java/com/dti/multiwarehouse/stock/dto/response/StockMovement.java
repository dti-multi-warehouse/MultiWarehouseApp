package com.dti.multiwarehouse.stock.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StockMovement {
    private Instant date;
    private int quantity;
    private String source;
    private int note;

    public static StockMovement fromDto(RetrieveStockDetails dto) {
        return StockMovement.builder()
                .date(dto.getCreatedAt())
                .quantity(dto.getQuantity())
                .source(dto.getSource())
                .note(dto.getNote())
                .build();
    }
}
