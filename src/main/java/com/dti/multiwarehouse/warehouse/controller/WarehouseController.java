package com.dti.multiwarehouse.warehouse.controller;

import com.dti.multiwarehouse.response.Response;
import com.dti.multiwarehouse.warehouse.service.WarehouseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1")
public class WarehouseController {
    private final WarehouseService warehouseService;

    @GetMapping
    public ResponseEntity<?> getAllWarehouses() {
        var res = warehouseService.getAllWarehouses();
        return Response.success("Successfully retrieved all warehouses", res);
    }

    @PostMapping
    public ResponseEntity<?> createWarehouse() {
        warehouseService.createWarehouse();
        return Response.success("Successfully created a warehouse");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteWarehouse(@PathVariable Long id) {
        warehouseService.deleteWarehouse(id);
        return Response.success("Successfully deleted a warehouse");
    }
}
