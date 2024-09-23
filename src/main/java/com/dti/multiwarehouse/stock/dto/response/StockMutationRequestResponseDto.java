package com.dti.multiwarehouse.stock.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StockMutationRequestResponseDto {
    Long id;
    Long warehouseFromId;
    Long warehouseToId;
    int quantity;
    String productName;
    String thumbnail;
    Instant createdAt;
}
