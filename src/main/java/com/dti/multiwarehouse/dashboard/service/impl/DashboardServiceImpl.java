package com.dti.multiwarehouse.dashboard.service.impl;

import com.dti.multiwarehouse.dashboard.dto.response.GetMonthlySalesReportResponseDto;
import com.dti.multiwarehouse.dashboard.dto.response.GetMonthlyStockSummaryResponseDto;
import com.dti.multiwarehouse.dashboard.service.DashboardService;
import com.dti.multiwarehouse.order.repository.OrderRepository;
import com.dti.multiwarehouse.stock.repository.StockMutationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class DashboardServiceImpl implements DashboardService {

    private final OrderRepository orderRepository;
    private final StockMutationRepository stockMutationRepository;

    @Override
    public int getMonthlyTotalSalesReport(Long warehouseId, Date currentDate) {
        return orderRepository.getMonthlyTotalSalesReport(warehouseId, currentDate);
    }

    @Override
    public List<GetMonthlySalesReportResponseDto> getMonthlyProductSalesReport(Long warehouseId, Date currentDate) {
        var res = orderRepository.getMonthlyProductSalesReport(warehouseId, currentDate);
        return res.stream()
                .map(GetMonthlySalesReportResponseDto::fromDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<GetMonthlySalesReportResponseDto> getMonthlyCategorySalesReport(Long warehouseId, Date currentDate) {
        var res = orderRepository.getMonthlyCategorySalesReport(warehouseId, currentDate);
        return res.stream()
                .map(GetMonthlySalesReportResponseDto::fromDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<GetMonthlyStockSummaryResponseDto> getMonthlyStockSummaryReport(Long warehouseId, Date currentDate) {
        var res = stockMutationRepository.getMonthlyStockSummary(warehouseId, currentDate);
        return res.stream()
                .map(GetMonthlyStockSummaryResponseDto::fromDto)
                .collect(Collectors.toList());
    }
}
