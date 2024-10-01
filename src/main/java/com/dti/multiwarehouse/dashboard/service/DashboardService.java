package com.dti.multiwarehouse.dashboard.service;

import com.dti.multiwarehouse.dashboard.dto.response.GetProductCategorySalesReportResponseDto;
import com.dti.multiwarehouse.dashboard.dto.response.GetMonthlyStockSummaryResponseDto;
import com.dti.multiwarehouse.dashboard.dto.response.GetTotalSalesResponseDto;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

public interface DashboardService {
    GetTotalSalesResponseDto getMonthlyTotalSalesReport(Long warehouseId, LocalDate currentDate);
    List<GetProductCategorySalesReportResponseDto> getMonthlyProductSalesReport(Long warehouseId, Date currentDate);
    List<GetProductCategorySalesReportResponseDto> getMonthlyCategorySalesReport(Long warehouseId, Date currentDate);
    List<GetMonthlyStockSummaryResponseDto> getMonthlyStockSummaryReport(Long warehouseId, Date currentDate);
}
