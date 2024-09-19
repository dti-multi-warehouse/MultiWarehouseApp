package com.dti.multiwarehouse.address.service.Impl;

import com.dti.multiwarehouse.address.dto.UserAddressDTO;
import com.dti.multiwarehouse.address.entity.Address;
import com.dti.multiwarehouse.address.entity.UserAddress;
import com.dti.multiwarehouse.address.repository.AddressRepository;
import com.dti.multiwarehouse.address.repository.UserAddressRepository;
import com.dti.multiwarehouse.address.service.AddressService;
import com.dti.multiwarehouse.user.entity.User;
import com.dti.multiwarehouse.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class AddressServiceImpl implements AddressService {
    private final AddressRepository addressRepository;
    private final UserAddressRepository userAddressRepository;
    private final UserRepository userRepository;

    public AddressServiceImpl(AddressRepository addressRepository, UserAddressRepository userAddressRepository, UserRepository userRepository) {
        this.addressRepository = addressRepository;
        this.userAddressRepository = userAddressRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public UserAddress saveUserAddress(UserAddressDTO dto, Long userId) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (!userOptional.isPresent()) {
            throw new IllegalArgumentException("User not found");
        }

        User user = userOptional.get();

        if (Boolean.TRUE.equals(dto.getIsPrimary())) {
            userAddressRepository.updateIsPrimaryToFalseForUser(user.getId());
        }

        Address address = new Address();
        address.setStreet(dto.getStreet());
        address.setCity(dto.getCity());
        address.setProvince(dto.getProvince());
        address.setLatitude(dto.getLatitude());
        address.setLongitude(dto.getLongitude());

        Address savedAddress = addressRepository.save(address);

        UserAddress userAddress = new UserAddress();
        userAddress.setName(dto.getName());
        userAddress.setPhoneNumber(dto.getPhoneNumber());
        userAddress.setLabel(dto.getLabel());
        userAddress.setPrimary(dto.getIsPrimary() != null ? dto.getIsPrimary() : false);
        userAddress.setUser(user);
        userAddress.setAddress(savedAddress);

        return userAddressRepository.save(userAddress);
    }

    @Override
    public Optional<UserAddress> getUserAddressById(Long id) {
        return userAddressRepository.findById(id);
    }

    @Override
    public List<UserAddress> getUserAddresses(Long userId) {
        return userAddressRepository.findByUserId(userId);
    }

    @Override
    @Transactional
    public void deleteAddressById(Long id) {
        Optional<UserAddress> userAddressOptional = userAddressRepository.findById(id);

        if (!userAddressOptional.isPresent()) {
            throw new IllegalArgumentException("User address not found");
        }

        UserAddress userAddress = userAddressOptional.get();
        Address associatedAddress = userAddress.getAddress();

        userAddressRepository.deleteById(id);

        long addressUsageCount = userAddressRepository.countByAddressId(associatedAddress.getId());

        if (addressUsageCount == 0) {
            addressRepository.deleteById(associatedAddress.getId());
        }
    }

    @Override
    public UserAddress updateUserAddress(Long addressId, UserAddressDTO dto, Long userId) {
        Optional<UserAddress> userAddressOptional = userAddressRepository.findById(addressId);
        if (!userAddressOptional.isPresent()) {
            throw new IllegalArgumentException("User address not found");
        }

        Optional<User> userOptional = userRepository.findById(userId);
        if (!userOptional.isPresent()) {
            throw new IllegalArgumentException("User not found");
        }

        User user = userOptional.get();
        UserAddress userAddress = userAddressOptional.get();

        if (Boolean.TRUE.equals(dto.getIsPrimary())) {
            userAddressRepository.updateIsPrimaryToFalseForUser(user.getId());
        }

        Address address = userAddress.getAddress();
        address.setStreet(dto.getStreet());
        address.setCity(dto.getCity());
        address.setProvince(dto.getProvince());
        address.setLatitude(dto.getLatitude());
        address.setLongitude(dto.getLongitude());

        Address updatedAddress = addressRepository.save(address);

        userAddress.setName(dto.getName());
        userAddress.setPhoneNumber(dto.getPhoneNumber());
        userAddress.setLabel(dto.getLabel());
        userAddress.setPrimary(dto.getIsPrimary() != null ? dto.getIsPrimary() : false);
        userAddress.setAddress(updatedAddress);

        return userAddressRepository.save(userAddress);
    }
}
