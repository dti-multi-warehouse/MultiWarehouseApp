package com.dti.multiwarehouse.stock.service;

import com.dti.multiwarehouse.cart.dto.CartItem;
import com.dti.multiwarehouse.stock.dao.Stock;
import com.dti.multiwarehouse.stock.dto.request.RequestMutationRequestDto;
import com.dti.multiwarehouse.stock.dto.request.RestockRequestDto;
import com.dti.multiwarehouse.stock.dto.response.GetProductAndStockAvailabilityDto;
import com.dti.multiwarehouse.stock.dto.response.GetStockResponseDto;
import com.dti.multiwarehouse.stock.dto.response.RetrieveProductAndStockAvailabilityDto;
import com.dti.multiwarehouse.stock.dto.response.StockMutationRequestResponseDto;
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
    List<GetStockResponseDto> getAllStock();
    List<StockMutationRequestResponseDto> getStockMutationRequest();
    List<GetProductAndStockAvailabilityDto> getProductAndStockAvailability(Long warehouseId);
}
