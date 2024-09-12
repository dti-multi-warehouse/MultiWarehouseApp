package com.dti.multiwarehouse.warehouse.repository;

import com.dti.multiwarehouse.warehouse.dao.Warehouse;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WarehouseRepository extends JpaRepository<Warehouse, Long> {
}
