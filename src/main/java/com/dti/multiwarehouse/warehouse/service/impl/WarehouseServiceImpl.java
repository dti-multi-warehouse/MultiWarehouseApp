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
import com.dti.multiwarehouse.warehouse.dto.WarehouseListResponseDto;
import com.dti.multiwarehouse.warehouse.mapper.WarehouseMapper;
import com.dti.multiwarehouse.warehouse.repository.WarehouseRepository;
import com.dti.multiwarehouse.warehouse.service.WarehouseService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
    public Page<WarehouseDTO> searchWarehouses(String name, String city, String province, String sortField, String sortDirection, int page, int size) {
        Sort.Direction direction = Sort.Direction.ASC;
        if ("desc".equalsIgnoreCase(sortDirection)) {
            direction = Sort.Direction.DESC;
        }

        String mappedSortField;
        switch (sortField) {
            case "name":
                mappedSortField = "name";
                break;
            case "city":
                mappedSortField = "warehouseAddress.address.city";
                break;
            case "province":
                mappedSortField = "warehouseAddress.address.province";
                break;
            default:
                throw new IllegalArgumentException("Invalid sort field: " + sortField);
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, mappedSortField));
        Page<Warehouse> warehouses = warehouseRepository.searchWarehouses(name, city, province, pageable);

        return warehouses.map(this::mapToWarehouseDTO);
    }

    public WarehouseDTO mapToWarehouseDTO(Warehouse warehouse) {
        WarehouseDTO dto = new WarehouseDTO();
        dto.setId(warehouse.getId());
        dto.setName(warehouse.getName());
        dto.setStreet(warehouse.getWarehouseAddress().getAddress().getStreet());
        dto.setCity(warehouse.getWarehouseAddress().getAddress().getCity());
        dto.setProvince(warehouse.getWarehouseAddress().getAddress().getProvince());
        dto.setLatitude(warehouse.getWarehouseAddress().getAddress().getLatitude());
        dto.setLongitude(warehouse.getWarehouseAddress().getAddress().getLongitude());
        dto.setAdminUsername(warehouse.getWarehouseAdmins().isEmpty() ? "Unassigned" : warehouse.getWarehouseAdmins().get(0).getUser().getUsername());

        return dto;
    }

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
        warehouse.setName(dto.getName());

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
        warehouse.setName(dto.getName());
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

        if (!warehouseOpt.isPresent()) {
            throw new IllegalArgumentException("Invalid warehouse ID");
        }

        Warehouse warehouse = warehouseOpt.get();
        if (dto.getUserId() == null) {
            Optional<WarehouseAdmin> existingAdminForWarehouseOpt = warehouseAdminRepository.findByWarehouse(warehouse);
            if (existingAdminForWarehouseOpt.isPresent()) {
                warehouseAdminRepository.delete(existingAdminForWarehouseOpt.get());
            }
            return;
        }

        Optional<User> userOpt = userRepository.findById(dto.getUserId());
        if (!userOpt.isPresent()) {
            throw new IllegalArgumentException("Invalid user ID");
        }

        User user = userOpt.get();
        Optional<WarehouseAdmin> existingAdminForUserOpt = warehouseAdminRepository.findByUser(user);
        if (existingAdminForUserOpt.isPresent() &&
                !existingAdminForUserOpt.get().getWarehouse().getId().equals(dto.getWarehouseId())) {
            throw new IllegalArgumentException("This user is already assigned to another warehouse.");
        }

        Optional<WarehouseAdmin> existingAdminForWarehouseOpt = warehouseAdminRepository.findByWarehouse(warehouse);
        if (existingAdminForWarehouseOpt.isPresent()) {
            warehouseAdminRepository.delete(existingAdminForWarehouseOpt.get());
        }

        if (!user.getRole().equalsIgnoreCase("warehouse_admin")) {
            throw new IllegalArgumentException("Only users with 'warehouse_admin' role can be assigned to a warehouse.");
        }

        WarehouseAdmin newAdmin = new WarehouseAdmin();
        newAdmin.setUser(user);
        newAdmin.setWarehouse(warehouse);
        warehouseAdminRepository.save(newAdmin);
    }

    @Override
    public List<WarehouseListResponseDto> getWarehouseList() {
        var warehouses = warehouseRepository.findAll();
        return warehouses.stream()
                .map(WarehouseListResponseDto::fromEntity)
                .toList();
    }

    @Override
    public Warehouse findFirstWarehouse() {
        return warehouseRepository.findFirstWarehouse();
    }

    @Override
    public List<Warehouse> findNearbyWarehouses(Long warehouseId, double longitude, double latitude) {
        return warehouseRepository.findNearbyWarehouses(warehouseId, longitude, latitude);
    }
}
