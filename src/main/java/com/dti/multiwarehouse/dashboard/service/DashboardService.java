package com.dti.multiwarehouse.dashboard.service;

import com.dti.multiwarehouse.dashboard.dto.response.GetMonthlySalesReport;

import java.util.Date;
import java.util.List;

public interface DashboardService {
    int getMonthlyTotalSalesReport(Long warehouseId, Date currentDate);
    List<GetMonthlySalesReport> getMonthlyProductSalesReport(Long warehouseId, Date currentDate);
    List<GetMonthlySalesReport> getMonthlyCategorySalesReport(Long warehouseId, Date currentDate);
}
