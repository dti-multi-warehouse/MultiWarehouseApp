package com.dti.multiwarehouse.warehouse.service;

import com.dti.multiwarehouse.cart.dto.CartItem;
import com.dti.multiwarehouse.warehouse.dao.Warehouse;
import com.dti.multiwarehouse.warehouse.dto.AssignWarehouseAdminDTO;
import com.dti.multiwarehouse.warehouse.dto.WarehouseDTO;

import java.util.List;
import java.util.Optional;

public interface WarehouseService {
    Warehouse createWarehouse(WarehouseDTO dto);
    Warehouse updateWarehouse(Long id, WarehouseDTO dto);
    void deleteWarehouse(Long id);
    Warehouse findWarehouseById(Long id);
    List<Warehouse> getAllWarehouses();
    Warehouse getWarehouseById(Long id);
    void assignWarehouseAdmin(AssignWarehouseAdminDTO dto);
}
