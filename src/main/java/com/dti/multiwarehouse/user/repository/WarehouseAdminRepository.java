package com.dti.multiwarehouse.user.repository;

import com.dti.multiwarehouse.user.entity.WarehouseAdmin;
import com.dti.multiwarehouse.warehouse.dao.Warehouse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface WarehouseAdminRepository extends JpaRepository<WarehouseAdmin, Long> {
    Optional<WarehouseAdmin> findByWarehouse(Warehouse warehouse);
    @Modifying
    @Query("DELETE FROM WarehouseAdmin wa WHERE wa.warehouse = :warehouse")
    void deleteByWarehouse(@Param("warehouse") Warehouse warehouse);

}
