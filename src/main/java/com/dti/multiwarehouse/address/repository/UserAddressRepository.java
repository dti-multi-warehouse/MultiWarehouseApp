package com.dti.multiwarehouse.address.repository;

import com.dti.multiwarehouse.address.entity.UserAddress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface UserAddressRepository extends JpaRepository<UserAddress, Long> {
    List<UserAddress> findByUserId(Long userId);
    @Modifying
    @Transactional
    @Query("UPDATE UserAddress ua SET ua.isPrimary = false WHERE ua.user.id = :userId AND ua.isPrimary = true")
    void updateIsPrimaryToFalseForUser(Long userId);

    @Query("SELECT COUNT(ua) FROM UserAddress ua WHERE ua.address.id = :addressId")
    long countByAddressId(Long addressId);
}
