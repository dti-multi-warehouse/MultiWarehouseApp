package com.dti.multiwarehouse.dashboard.controller;

import com.dti.multiwarehouse.dashboard.service.DashboardService;
import com.dti.multiwarehouse.response.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/dashboard")
public class DashboardController {
    private final DashboardService dashboardService;

    @GetMapping()
    @PreAuthorize("hasAnyRole('admin', 'warehouse_admin')")
    public ResponseEntity<?> getMonthlyTotalSalesReport(
            @AuthenticationPrincipal Jwt jwt,
            @RequestParam Long warehouseId,
            @RequestParam @DateTimeFormat(pattern="yyyy-MM-dd") LocalDate date
            ) {
        if (
                jwt == null ||
                        jwt.getClaim("warehouse_id") == null ||
                        (!jwt.getClaim("warehouse_id").equals(warehouseId) &&
                        !jwt.getClaim("role").equals("admin"))
        ) {
            return Response.failed("Invalid authority");
        }
        var res =  dashboardService.getSalesReport(warehouseId, date);
        return Response.success("Successfully retrieved monthly total sales report", res);
    }
}
