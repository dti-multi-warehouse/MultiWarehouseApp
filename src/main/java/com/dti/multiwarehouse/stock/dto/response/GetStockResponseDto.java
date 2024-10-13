package com.dti.multiwarehouse.stock.dto.response;

import lombok.Getter;

import java.util.List;

@Getter
public class GetStockResponseDto {
    private final int page;
    private final int totalPage;
    private final List<StockDto> stocks;

    public GetStockResponseDto(int page, int totalPage, List<StockDto> stocks) {
        this.page = page;
        this.totalPage = totalPage;
        this.stocks = stocks;
    }
}
