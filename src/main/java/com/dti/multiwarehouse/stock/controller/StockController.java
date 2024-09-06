package com.dti.multiwarehouse.stock.controller;

import com.dti.multiwarehouse.stock.dto.request.RestockRequestDto;
import com.dti.multiwarehouse.stock.service.StockService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/stock")
public class StockController {
    private final StockService stockService;

    @PostMapping("/restock")
    public ResponseEntity<?> restock(@RequestBody RestockRequestDto requestDto) {
        stockService.restock(requestDto);
        return ResponseEntity.ok().build();
    }
}
