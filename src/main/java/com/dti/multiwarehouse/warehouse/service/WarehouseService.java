package com.dti.multiwarehouse.warehouse.service;

import com.dti.multiwarehouse.warehouse.dao.Warehouse;

import java.util.List;

public interface WarehouseService {
    void createWarehouse();
    List<Warehouse> getAllWarehouses();
    void deleteWarehouse(Long id);
}
