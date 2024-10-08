package com.dti.multiwarehouse.stock.controller;

import com.dti.multiwarehouse.response.Response;
import com.dti.multiwarehouse.stock.dto.request.RequestMutationRequestDto;
import com.dti.multiwarehouse.stock.dto.request.RestockRequestDto;
import com.dti.multiwarehouse.stock.service.StockService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/stock")
public class StockController {
    private final StockService stockService;

    @GetMapping
    public ResponseEntity<?> getAllStocks(
            @RequestParam Long warehouseId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(defaultValue = "") String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int perPage
    ) {
        if (date == null) {
            date = LocalDate.now();
        }
        var res = stockService.getAllStock(warehouseId, date, query, page, perPage);
        return Response.success("Successfully retrieved stocks", res);
    }

    @GetMapping("/details")
    public ResponseEntity<?> getStockDetails(
            @RequestParam Long warehouseId,
            @RequestParam Long productId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        if (date == null) {
            date = LocalDate.now();
        }
        var res = stockService.getStockDetails(warehouseId, productId, date);
        return Response.success("Successfully retrieved stock details", res);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getProductAndStockAvailability(@PathVariable("id") Long id) {
        var res = stockService.getProductAndStockAvailability(id);
        return Response.success("Successfully retrieved stock", res);
    }

    @GetMapping("/warehouse")
    public ResponseEntity<?> getWarehouseAndStockAvailability(@RequestParam Long warehouseId, @RequestParam Long productId) {
        var res = stockService.getWarehouseAndStockAvailability(warehouseId, productId);
        return Response.success("Successfully retrieved stock", res);
    }

    @PostMapping("/restock")
    public ResponseEntity<?> restock(@Valid @RequestBody RestockRequestDto requestDto) {
        stockService.restock(requestDto);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/mutation")
    public ResponseEntity<?> requestMutation(@Valid @RequestBody RequestMutationRequestDto requestDto) {
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
