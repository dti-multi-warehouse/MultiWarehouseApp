package com.dti.multiwarehouse.stock.controller;

import com.dti.multiwarehouse.response.Response;
import com.dti.multiwarehouse.stock.dto.request.RequestMutationRequestDto;
import com.dti.multiwarehouse.stock.dto.request.RestockRequestDto;
import com.dti.multiwarehouse.stock.service.StockService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/stock")
public class StockController {
    private final StockService stockService;

    @GetMapping
    public ResponseEntity<?> getAllStocks(
            @AuthenticationPrincipal Jwt jwt,
            @RequestParam Long warehouseId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(defaultValue = "") String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int perPage
    ) {
        if (date == null) {
            date = LocalDate.now();
        }
        if (
                jwt == null ||
                        jwt.getClaim("warehouse_id") == null ||
                        (!jwt.getClaim("warehouse_id").equals(warehouseId) && !jwt.getClaim("role").equals("admin"))
        ) {
            return Response.failed("Invalid authority");
        }
        var res = stockService.getAllStock(warehouseId, date, query, page, perPage);
        return Response.success("Successfully retrieved stocks", res);
    }

    @GetMapping("/details")
    public ResponseEntity<?> getStockDetails(
            @AuthenticationPrincipal Jwt jwt,
            @RequestParam Long warehouseId,
            @RequestParam Long productId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        if (date == null) {
            date = LocalDate.now();
        }
        if (
                jwt == null ||
                        jwt.getClaim("warehouse_id") == null ||
                        (!jwt.getClaim("warehouse_id").equals(warehouseId) && !jwt.getClaim("role").equals("admin"))
        ) {
            return Response.failed("Invalid authority");
        }
        var res = stockService.getStockDetails(warehouseId, productId, date);
        return Response.success("Successfully retrieved stock details", res);
    }

    @GetMapping("/{warehouseId}")
    public ResponseEntity<?> getProductAndStockAvailability(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable("warehouseId") Long warehouseId
    ) {
        if (
                jwt == null ||
                        jwt.getClaim("warehouse_id") == null ||
                        (!jwt.getClaim("warehouse_id").equals(warehouseId) && !jwt.getClaim("role").equals("admin"))
        ) {
            return Response.failed("Invalid authority");
        }
        var res = stockService.getProductAndStockAvailability(warehouseId);
        return Response.success("Successfully retrieved stock", res);
    }

    @GetMapping("/warehouse")
    public ResponseEntity<?> getWarehouseAndStockAvailability(
            @AuthenticationPrincipal Jwt jwt,
            @RequestParam Long warehouseId,
            @RequestParam Long productId
    ) {
        if (
                jwt == null ||
                        jwt.getClaim("warehouse_id") == null ||
                        (!jwt.getClaim("warehouse_id").equals(warehouseId) && !jwt.getClaim("role").equals("admin"))
        ) {
            return Response.failed("Invalid authority");
        }
        var res = stockService.getWarehouseAndStockAvailability(warehouseId, productId);
        return Response.success("Successfully retrieved stock", res);
    }

    @PostMapping("/restock")
    public ResponseEntity<?> restock(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody RestockRequestDto requestDto
    ) {
        if (
                jwt == null ||
                        jwt.getClaim("warehouse_id") == null ||
                        (!jwt.getClaim("warehouse_id").equals(requestDto.getWarehouseToId()) && !jwt.getClaim("role").equals("admin"))
        ) {
            return Response.failed("Invalid authority");
        }
        stockService.restock(requestDto);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/mutation")
    public ResponseEntity<?> requestMutation(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody RequestMutationRequestDto requestDto
    ) {
        if (
                jwt == null ||
                        jwt.getClaim("warehouse_id") == null ||
                        (!jwt.getClaim("warehouse_id").equals(requestDto.getWarehouseToId()) && !jwt.getClaim("role").equals("admin"))
        ) {
            return Response.failed("Invalid authority");
        }
        stockService.requestStockMutation(requestDto);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/mutation/{id}")
    public ResponseEntity<?> getActiveMutationRequests(@PathVariable Long id) {
        var res = stockService.getStockMutationRequest(id);
        return Response.success("Successfully retrieved stock mutation requests", res);
    }

    @PutMapping("/mutation/{id}/accept")
    public ResponseEntity<?> acceptMutation(@PathVariable Long id) {
        stockService.acceptStockMutation(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/mutation/{id}/reject")
    public ResponseEntity<?> rejectMutation(@PathVariable Long id) {
        stockService.rejectStockMutation(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/mutation/{id}/cancel")
    public ResponseEntity<?> cancelMutation(@PathVariable Long id) {
        stockService.cancelStockMutation(id);
        return ResponseEntity.ok().build();
    }
}
