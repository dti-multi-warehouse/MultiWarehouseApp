package com.dti.multiwarehouse.address.service;

import com.dti.multiwarehouse.address.dto.UserAddressDTO;
import com.dti.multiwarehouse.address.entity.UserAddress;

import java.util.List;
import java.util.Optional;

public interface AddressService {
    UserAddress saveUserAddress(UserAddressDTO dto, Long userId);
    Optional<UserAddress> getUserAddressById(Long id);
    List<UserAddress> getUserAddresses(Long userId);
    void deleteAddressById(Long id);
    UserAddress updateUserAddress(Long addressId, UserAddressDTO dto, Long userId);
}
