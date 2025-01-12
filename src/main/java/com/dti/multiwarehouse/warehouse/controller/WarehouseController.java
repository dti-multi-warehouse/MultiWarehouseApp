package com.dti.multiwarehouse.warehouse.controller;

import com.dti.multiwarehouse.response.Response;
import com.dti.multiwarehouse.warehouse.dao.Warehouse;
import com.dti.multiwarehouse.warehouse.dto.AssignWarehouseAdminDTO;
import com.dti.multiwarehouse.warehouse.dto.WarehouseDTO;
import com.dti.multiwarehouse.warehouse.service.WarehouseService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/warehouse")
public class WarehouseController {

    private final WarehouseService warehouseService;

    @GetMapping("/search")
    public ResponseEntity<?> searchWarehouses(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String province,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sortField,
            @RequestParam(defaultValue = "asc") String sortDirection
    ) {
        name = (name != null && name.trim().isEmpty()) ? null : name;
        city = (city != null && city.trim().isEmpty()) ? null : city;
        province = (province != null && province.trim().isEmpty()) ? null : province;

        Page<WarehouseDTO> warehouses = warehouseService.searchWarehouses(name, city, province, sortField, sortDirection, page, size);
        return Response.success("Warehouses retrieved successfully", warehouses);
    }

    @PostMapping("/create")
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<?> createWarehouse(@RequestBody WarehouseDTO dto) {
        Warehouse createdWarehouse = warehouseService.createWarehouse(dto);
        return ResponseEntity.ok(createdWarehouse);
    }

    @PutMapping("/update/{id}")
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<?> updateWarehouse(@PathVariable Long id, @RequestBody WarehouseDTO dto) {
        Warehouse updatedWarehouse = warehouseService.updateWarehouse(id, dto);
        return ResponseEntity.ok(updatedWarehouse);
    }

    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<?> deleteWarehouse(@PathVariable Long id) {
        warehouseService.deleteWarehouse(id);
        return ResponseEntity.ok("Warehouse deleted successfully");
    }

    @GetMapping("/all")
    public ResponseEntity<Response<List<Warehouse>>> getAllWarehouses() {
        var warehouses = warehouseService.getAllWarehouses();
        return Response.success("Warehouses retrieved successfully", warehouses);
    }

    @PostMapping("/assign-admin")
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<?> assignWarehouseAdmin(@RequestBody AssignWarehouseAdminDTO dto) {
        try {
            warehouseService.assignWarehouseAdmin(dto);
            return ResponseEntity.ok("Warehouse admin assigned successfully");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("An error occurred while assigning the warehouse admin");
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getWarehouseById(@PathVariable Long id) {
        try {
            Warehouse warehouse = warehouseService.findWarehouseById(id);
            return ResponseEntity.ok(warehouse);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(404).body("Warehouse not found");
        }
    }

    @GetMapping("/list")
    public ResponseEntity<?> getWarehouseList() {
        var res = warehouseService.getWarehouseList();
        return Response.success("Warehouses retrieved successfully", res);
    }
}