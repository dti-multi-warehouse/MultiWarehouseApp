package com.dti.multiwarehouse.warehouse.service;

import com.dti.multiwarehouse.cart.dto.CartItem;
import com.dti.multiwarehouse.warehouse.dao.Warehouse;
import com.dti.multiwarehouse.warehouse.dto.AssignWarehouseAdminDTO;
import com.dti.multiwarehouse.warehouse.dto.WarehouseDTO;
import com.dti.multiwarehouse.warehouse.dto.WarehouseListResponseDto;
import org.springframework.data.domain.Page;

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
    Page<WarehouseDTO> searchWarehouses(String name, String city, String province, String sortField, String sortDirection, int page, int size);
    List<WarehouseListResponseDto> getWarehouseList();
    Warehouse findFirstWarehouse();
    List<Warehouse> findNearbyWarehouses(Long warehouseId, double longitude, double latitude);
}
