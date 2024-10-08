package com.dti.multiwarehouse.dashboard.service;

import com.dti.multiwarehouse.dashboard.dto.response.*;

import java.time.LocalDate;
import java.util.List;

public interface DashboardService {
    GetSalesReportDto getSalesReport(Long warehouseId, LocalDate date);
    List<GetMonthlyStockSummaryResponseDto> getMonthlyStockSummaryReport(Long warehouseId, LocalDate currentDate);
}
