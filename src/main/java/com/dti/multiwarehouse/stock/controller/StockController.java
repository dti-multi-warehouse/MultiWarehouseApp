package com.dti.multiwarehouse.stock.controller;

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

    @PutMapping("/mutation/accept/{id}")
    public ResponseEntity<?> acceptMutation(@PathVariable Long id) {
        stockService.acceptStockMutation(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/mutation/reject/{id}")
    public ResponseEntity<?> rejectMutation(@PathVariable Long id) {
        stockService.rejectStockMutation(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/mutation/cancel/{id}")
    public ResponseEntity<?> cancelMutation(@PathVariable Long id) {
        stockService.cancelStockMutation(id);
        return ResponseEntity.ok().build();
    }
}
