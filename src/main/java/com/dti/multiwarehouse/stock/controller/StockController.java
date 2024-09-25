package com.dti.multiwarehouse.stock.controller;

import com.dti.multiwarehouse.response.Response;
import com.dti.multiwarehouse.stock.dto.request.GetWarehouseAndStockAvailabililtyRequestDto;
import com.dti.multiwarehouse.stock.dto.request.RequestMutationRequestDto;
import com.dti.multiwarehouse.stock.dto.request.RestockRequestDto;
import com.dti.multiwarehouse.stock.service.StockService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/stock")
public class StockController {
    private final StockService stockService;

    @GetMapping
    public ResponseEntity<?> getAllStocks() {
        var res = stockService.getAllStock();
        return Response.success("Successfully retrieved stocks", res);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getProductAndStockAvailability(@PathVariable("id") Long id) {
        var res = stockService.getProductAndStockAvailability(id);
        return Response.success("Successfully retrieved stock", res);
    }

    @GetMapping("/warehouse")
    public ResponseEntity<?> getWarehouseAndStockAvailability(@RequestBody GetWarehouseAndStockAvailabililtyRequestDto requestDto) {
        var res = stockService.getWarehouseAndStockAvailability(requestDto.getWarehouseId(), requestDto.getProductId());
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

    @GetMapping("/mutation")
    public ResponseEntity<?> getActiveMutationRequests() {
        var res = stockService.getStockMutationRequest();
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
