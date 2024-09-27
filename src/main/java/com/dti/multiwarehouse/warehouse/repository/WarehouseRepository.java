package com.dti.multiwarehouse.warehouse.repository;

import com.dti.multiwarehouse.warehouse.dao.Warehouse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface WarehouseRepository extends JpaRepository<Warehouse, Long> {
    @Query("SELECT w FROM Warehouse w WHERE " +
            "(:name IS NULL OR w.name LIKE %:name%) AND " +
            "(:city IS NULL OR w.warehouseAddress.address.city LIKE %:city%) AND " +
            "(:province IS NULL OR w.warehouseAddress.address.province LIKE %:province%)")
    Page<Warehouse> searchWarehouses(String name, String city, String province, Pageable pageable);
}
