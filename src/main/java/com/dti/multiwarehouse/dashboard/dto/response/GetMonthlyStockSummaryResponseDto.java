package com.dti.multiwarehouse.dashboard.dto.response;

import lombok.Getter;

@Getter
public class GetMonthlyStockSummaryResponseDto {
    private final Long id;
    private final String name;
    private final int incoming;
    private final int outgoing;
    private final int stock;

    public GetMonthlyStockSummaryResponseDto(RetrieveMonthlyStockSummary dto) {
        this.id = dto.getId();
        this.name = dto.getName();
        this.incoming = dto.getIncoming();
        this.outgoing = dto.getOutgoing();
        this.stock = dto.getStock();
    }
}
