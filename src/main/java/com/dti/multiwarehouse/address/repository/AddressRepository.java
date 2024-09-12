package com.dti.multiwarehouse.address.repository;

import com.dti.multiwarehouse.address.entity.Address;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AddressRepository extends JpaRepository<Address, Long> {
}
