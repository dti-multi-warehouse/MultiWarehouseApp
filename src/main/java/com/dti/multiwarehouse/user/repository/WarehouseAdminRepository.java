package com.dti.multiwarehouse.user.repository;

import com.dti.multiwarehouse.user.entity.WarehouseAdmin;
import com.dti.multiwarehouse.warehouse.dao.Warehouse;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WarehouseAdminRepository extends JpaRepository<WarehouseAdmin, Long> {
    Optional<WarehouseAdmin> findByWarehouse(Warehouse warehouse);

}
