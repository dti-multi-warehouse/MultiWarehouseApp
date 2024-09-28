package com.dti.multiwarehouse.stock.service;

import com.dti.multiwarehouse.cart.dto.CartItem;
import com.dti.multiwarehouse.stock.dto.request.RequestMutationRequestDto;
import com.dti.multiwarehouse.stock.dto.request.RestockRequestDto;
import com.dti.multiwarehouse.stock.dto.response.*;
import com.dti.multiwarehouse.stock.dto.response.GetStockResponseDto;
import com.dti.multiwarehouse.warehouse.dao.Warehouse;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
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
    List<GetStockResponseDto> getAllStock(Long warehouseId, LocalDate date);
    List<StockMutationRequestResponseDto> getStockMutationRequest();
    List<GetProductAndStockAvailabilityDto> getProductAndStockAvailability(Long warehouseId);
    List<GetWarehouseAndStockAvailabilityResponseDto> getWarehouseAndStockAvailability(Long warehouseId, Long productId);
    int getStockForProductInWarehouse(Long productId, Long warehouseId);
    Warehouse findWarehouseForOrder(List<CartItem> cartItems);
}
