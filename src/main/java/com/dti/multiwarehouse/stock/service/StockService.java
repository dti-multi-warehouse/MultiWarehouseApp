package com.dti.multiwarehouse.stock.service;

import com.dti.multiwarehouse.stock.dto.request.RestockRequestDto;

public interface StockService {
    void restock(RestockRequestDto requestDto);
    void requestStockMutation();
    void acceptStockMutation();
    void rejectStockMutation();
}
