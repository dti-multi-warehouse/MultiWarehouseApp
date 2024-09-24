package com.dti.multiwarehouse.order.service;

import com.dti.multiwarehouse.order.dto.request.ShippingCostRequestDto;
import com.dti.multiwarehouse.order.dto.response.ShippingCostResponseDto;
import com.dti.multiwarehouse.warehouse.dao.Warehouse;

public interface ShippingService {
    ShippingCostResponseDto calculateShippingCost(ShippingCostRequestDto shippingCostRequest);
    Warehouse findNearestWarehouse(Long userAddress);
}
