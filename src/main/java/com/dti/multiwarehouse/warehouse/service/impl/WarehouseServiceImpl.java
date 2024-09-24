package com.dti.multiwarehouse.warehouse.service.impl;

import com.dti.multiwarehouse.address.entity.Address;
import com.dti.multiwarehouse.address.entity.WarehouseAddress;
import com.dti.multiwarehouse.address.repository.AddressRepository;
import com.dti.multiwarehouse.cart.dto.CartItem;
import com.dti.multiwarehouse.stock.service.StockService;
import com.dti.multiwarehouse.user.entity.User;
import com.dti.multiwarehouse.user.entity.WarehouseAdmin;
import com.dti.multiwarehouse.user.repository.UserRepository;
import com.dti.multiwarehouse.user.repository.WarehouseAdminRepository;
import com.dti.multiwarehouse.warehouse.dao.Warehouse;
import com.dti.multiwarehouse.warehouse.dto.AssignWarehouseAdminDTO;
import com.dti.multiwarehouse.warehouse.dto.WarehouseDTO;
import com.dti.multiwarehouse.warehouse.repository.WarehouseRepository;
import com.dti.multiwarehouse.warehouse.service.WarehouseService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class WarehouseServiceImpl implements WarehouseService {
    private final WarehouseRepository warehouseRepository;
    private final AddressRepository addressRepository;
    private final UserRepository userRepository;
    private final WarehouseAdminRepository warehouseAdminRepository;

    @Override
    @Transactional
    public Warehouse createWarehouse(WarehouseDTO dto) {
        Address address = new Address();
        address.setStreet(dto.getStreet());
        address.setCity(dto.getCity());
        address.setProvince(dto.getProvince());
        address.setLatitude(dto.getLatitude());
        address.setLongitude(dto.getLongitude());
        Address savedAddress = addressRepository.save(address);

        Warehouse warehouse = new Warehouse();

        WarehouseAddress warehouseAddress = new WarehouseAddress();
        warehouseAddress.setAddress(savedAddress);
        warehouseAddress.setWarehouse(warehouse);

        warehouse.setWarehouseAddress(warehouseAddress);

        return warehouseRepository.save(warehouse);
    }

    @Override
    @Transactional
    public Warehouse updateWarehouse(Long id, WarehouseDTO dto) {
        Optional<Warehouse> warehouseOpt = warehouseRepository.findById(id);
        if (!warehouseOpt.isPresent()) {
            throw new IllegalArgumentException("Warehouse not found");
        }

        Warehouse warehouse = warehouseOpt.get();
        Address address = warehouse.getWarehouseAddress().getAddress();
        address.setStreet(dto.getStreet());
        address.setCity(dto.getCity());
        address.setProvince(dto.getProvince());
        address.setLatitude(dto.getLatitude());
        address.setLongitude(dto.getLongitude());

        addressRepository.save(address);
        return warehouseRepository.save(warehouse);
    }

    @Override
    @Transactional
    public void deleteWarehouse(Long id) {
        Optional<Warehouse> warehouseOpt = warehouseRepository.findById(id);
        if (!warehouseOpt.isPresent()) {
            throw new EntityNotFoundException("Warehouse not found");
        }

        Warehouse warehouse = warehouseOpt.get();
        Address address = warehouse.getWarehouseAddress().getAddress();

        warehouseRepository.deleteById(id);

        if (address != null) {
            addressRepository.delete(address);
        }
    }

    @Override
    public Warehouse findWarehouseById(Long id) {
        return warehouseRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Warehouse with id " + id + " not found"));
    }

    @Override
    public List<Warehouse> getAllWarehouses() {
        return warehouseRepository.findAll();
    }

    @Override
    public Warehouse getWarehouseById(Long id) {
        return warehouseRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Warehouse not found"));
    }

    @Override
    @Transactional
    public void assignWarehouseAdmin(AssignWarehouseAdminDTO dto) {
        Optional<Warehouse> warehouseOpt = warehouseRepository.findById(dto.getWarehouseId());
        Optional<User> userOpt = userRepository.findById(dto.getUserId());

        if (!warehouseOpt.isPresent() || !userOpt.isPresent()) {
            throw new IllegalArgumentException("Invalid warehouse or user");
        }

        Warehouse warehouse = warehouseOpt.get();
        User user = userOpt.get();

        WarehouseAdmin warehouseAdmin = new WarehouseAdmin();
        warehouseAdmin.setUser(user);
        warehouseAdmin.setWarehouse(warehouse);

        warehouseAdminRepository.save(warehouseAdmin);
    }
}
