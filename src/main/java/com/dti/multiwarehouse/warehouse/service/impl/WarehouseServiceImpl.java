package com.dti.multiwarehouse.warehouse.service.impl;

import com.dti.multiwarehouse.warehouse.dao.Warehouse;
import com.dti.multiwarehouse.warehouse.repository.WarehouseRepository;
import com.dti.multiwarehouse.warehouse.service.WarehouseService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@RequiredArgsConstructor
@Service
public class WarehouseServiceImpl implements WarehouseService {
    private final WarehouseRepository warehouseRepository;

    @Override
    public void createWarehouse() {
        warehouseRepository.save(new Warehouse());
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
    public void deleteWarehouse(Long id) {
        var warehouse = warehouseRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Warehouse with id " + id + " not found"));
        warehouse.setDeletedAt(Instant.now());
        warehouseRepository.save(warehouse);
    }
}
