package com.dti.multiwarehouse.dashboard.controller;

import com.dti.multiwarehouse.dashboard.service.DashboardService;
import com.dti.multiwarehouse.response.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.Date;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/dashboard")
public class DashboardController {
    private final DashboardService dashboardService;

    @GetMapping()
    public ResponseEntity<?> getMonthlyTotalSalesReport(
            @RequestParam Long warehouseId,
            @RequestParam @DateTimeFormat(pattern="yyyy-MM-dd") LocalDate date
            ) {
        var res =  dashboardService.getMonthlyTotalSalesReport(warehouseId, date);
        return Response.success("Successfully retrieved monthly total sales report", res);
    }

    @GetMapping("/product")
    public ResponseEntity<?> getMonthlyProductSalesReport(
            @RequestParam Long warehouseId,
            @RequestParam @DateTimeFormat(pattern="yyyy-MM-dd") Date date
    ) {
        var res =  dashboardService.getMonthlyProductSalesReport(warehouseId, date);
        return Response.success("Successfully retrieved monthly product sales report", res);
    }

    @GetMapping("/category")
    public ResponseEntity<?> getMonthlyCategorySalesReport(
            @RequestParam Long warehouseId,
            @RequestParam @DateTimeFormat(pattern="yyyy-MM-dd") Date date
    ) {
        var res =  dashboardService.getMonthlyCategorySalesReport(warehouseId, date);
        return Response.success("Successfully retrieved monthly category sales", res);
    }

    @GetMapping("/stock")
    public ResponseEntity<?> getMonthlyStockSalesReport(
            @RequestParam Long warehouseId,
            @RequestParam @DateTimeFormat(pattern="yyyy-MM-dd") Date date
    ) {
        var res = dashboardService.getMonthlyStockSummaryReport(warehouseId, date);
        return Response.success("Successfully retrieved monthly stock summary", res);
    }
}
