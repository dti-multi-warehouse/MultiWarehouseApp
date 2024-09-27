package com.dti.multiwarehouse.dashboard.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GetMonthlyStockSummaryResponseDto {
    private Long id;
    private String name;
    private int incoming;
    private int outgoing;
    private int stock;

    public static GetMonthlyStockSummaryResponseDto fromDto(RetrieveMonthlyStockSummary dto) {
        return GetMonthlyStockSummaryResponseDto.builder()
                .id(dto.getId())
                .name(dto.getName())
                .incoming(dto.getIncoming())
                .outgoing(dto.getOutgoing())
                .stock(dto.getStock())
                .build();
    }
}
