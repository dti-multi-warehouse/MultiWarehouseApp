package com.dti.multiwarehouse.stock.service;

import com.dti.multiwarehouse.cart.dto.CartItem;
import com.dti.multiwarehouse.order.dto.request.CreateOrderRequestDto;
import com.dti.multiwarehouse.stock.dto.request.RequestMutationRequestDto;
import com.dti.multiwarehouse.stock.dto.request.RestockRequestDto;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface StockService {
    @Transactional
    void restock(RestockRequestDto requestDto);
    @Transactional
    void requestStockMutation(RequestMutationRequestDto requestDto);
    @Transactional
    void acceptStockMutation(Long stockMutationId);
    @Transactional
    void cancelStockMutation(Long stockMutationId);
    @Transactional
    void rejectStockMutation(Long stockMutationId);
    @Transactional
    void processOrder(Long warehouseId, List<CartItem> cartItems);
}
