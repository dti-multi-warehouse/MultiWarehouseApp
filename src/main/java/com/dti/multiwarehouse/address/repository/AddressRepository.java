package com.dti.multiwarehouse.address.repository;

import com.dti.multiwarehouse.address.entity.Address;
import com.dti.multiwarehouse.address.entity.WarehouseAddress;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AddressRepository extends JpaRepository<Address, Long> {
    Optional<Address> findById(Long addressId);
}
