package com.dti.multiwarehouse.dashboard.service;

import com.dti.multiwarehouse.dashboard.dto.response.GetCategorySalesReportResponseDto;
import com.dti.multiwarehouse.dashboard.dto.response.GetMonthlyStockSummaryResponseDto;
import com.dti.multiwarehouse.dashboard.dto.response.GetProductSalesReportResponseDto;
import com.dti.multiwarehouse.dashboard.dto.response.GetTotalSalesResponseDto;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

public interface DashboardService {
    GetTotalSalesResponseDto getMonthlyTotalSalesReport(Long warehouseId, LocalDate currentDate);
    List<GetProductSalesReportResponseDto> getMonthlyProductSalesReport(Long warehouseId, Date currentDate);
    List<GetCategorySalesReportResponseDto> getMonthlyCategorySalesReport(Long warehouseId, Date currentDate);
    List<GetMonthlyStockSummaryResponseDto> getMonthlyStockSummaryReport(Long warehouseId, Date currentDate);
}
