package com.dti.multiwarehouse.dashboard.service;

import com.dti.multiwarehouse.dashboard.dto.response.GetMonthlySalesReportResponseDto;
import com.dti.multiwarehouse.dashboard.dto.response.GetMonthlyStockSummaryResponseDto;

import java.util.Date;
import java.util.List;

public interface DashboardService {
    int getMonthlyTotalSalesReport(Long warehouseId, Date currentDate);
    List<GetMonthlySalesReportResponseDto> getMonthlyProductSalesReport(Long warehouseId, Date currentDate);
    List<GetMonthlySalesReportResponseDto> getMonthlyCategorySalesReport(Long warehouseId, Date currentDate);
    List<GetMonthlyStockSummaryResponseDto> getMonthlyStockSummaryReport(Long warehouseId, Date currentDate);
}
