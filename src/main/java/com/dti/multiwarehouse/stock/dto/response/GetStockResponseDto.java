package com.dti.multiwarehouse.stock.dto.response;

import lombok.Getter;

@Getter
public class GetStockResponseDto {
    private final Long id;
    private final String name;
    private final int stock;
    private final String thumbnail;
    private final int incoming;
    private final int outgoing;

    public GetStockResponseDto(RetrieveStock dto) {
        this.id = dto.getId();
        this.name = dto.getName();
        this.stock = dto.getStock();
        this.thumbnail = dto.getThumbnail();
        this.incoming = dto.getIncoming();
        this.outgoing = dto.getOutgoing();
    }
}
