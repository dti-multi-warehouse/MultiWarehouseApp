package com.dti.multiwarehouse.address.repository;

import com.dti.multiwarehouse.address.entity.WarehouseAddress;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WarehouseAddressRepository extends JpaRepository<WarehouseAddress, Long> {
    Optional<WarehouseAddress> findById(Long warehouseId);
}
