package com.dti.multiwarehouse.warehouse.controller;

import com.dti.multiwarehouse.response.Response;
import com.dti.multiwarehouse.warehouse.dao.Warehouse;
import com.dti.multiwarehouse.warehouse.dto.AssignWarehouseAdminDTO;
import com.dti.multiwarehouse.warehouse.dto.WarehouseDTO;
import com.dti.multiwarehouse.warehouse.service.WarehouseService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/warehouse")
public class WarehouseController {

    private final WarehouseService warehouseService;

    @PostMapping("/create")
    public ResponseEntity<?> createWarehouse(@RequestBody WarehouseDTO dto) {
        Warehouse createdWarehouse = warehouseService.createWarehouse(dto);
        return ResponseEntity.ok(createdWarehouse);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateWarehouse(@PathVariable Long id, @RequestBody WarehouseDTO dto) {
        Warehouse updatedWarehouse = warehouseService.updateWarehouse(id, dto);
        return ResponseEntity.ok(updatedWarehouse);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteWarehouse(@PathVariable Long id) {
        warehouseService.deleteWarehouse(id);
        return ResponseEntity.ok("Warehouse deleted successfully");
    }

    @GetMapping("/all")
    public ResponseEntity<List<Warehouse>> getAllWarehouses() {
        return ResponseEntity.ok(warehouseService.getAllWarehouses());
    }

    @PostMapping("/assign-admin")
    public ResponseEntity<?> assignWarehouseAdmin(@RequestBody AssignWarehouseAdminDTO dto) {
        warehouseService.assignWarehouseAdmin(dto);
        return ResponseEntity.ok("Warehouse admin assigned successfully");
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getWarehouseById(@PathVariable Long id) {
        try {
            Warehouse warehouse = warehouseService.findWarehouseById(id);
            return ResponseEntity.ok(warehouse);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(404).body(null);
        }
    }
}