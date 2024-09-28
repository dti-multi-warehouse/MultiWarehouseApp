package com.dti.multiwarehouse.stock.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GetStockDetailsResponseDto {
    private Instant date;
    private int quantity;
    private String source;
    private int note;

    public static GetStockDetailsResponseDto fromDto(RetrieveStockDetails dto) {
        return GetStockDetailsResponseDto.builder()
                .date(dto.getCreatedAt())
                .quantity(dto.getQuantity())
                .source(dto.getSource())
                .note(dto.getNote())
                .build();
    }
}
