package com.dti.multiwarehouse.dashboard.service.impl;

import com.dti.multiwarehouse.dashboard.dto.response.*;
import com.dti.multiwarehouse.dashboard.service.DashboardService;
import com.dti.multiwarehouse.order.repository.OrderRepository;
import com.dti.multiwarehouse.stock.repository.StockMutationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class DashboardServiceImpl implements DashboardService {

    private final OrderRepository orderRepository;
    private final StockMutationRepository stockMutationRepository;

    @Override
    public GetTotalSalesResponseDto getMonthlyTotalSalesReport(Long warehouseId, LocalDate currentDate) {
        var totalRevenue = orderRepository.getMonthlyTotalSalesReport(warehouseId, currentDate);
        var sales = orderRepository
                .getMonthlySalesReport(warehouseId, currentDate)
                .stream()
                .map(Sales::fromDto)
                .toList();
        return new GetTotalSalesResponseDto(totalRevenue, sales);
    }

    @Override
    public List<GetProductSalesReportResponseDto> getMonthlyProductSalesReport(Long warehouseId, Date currentDate) {
        var res = orderRepository.getMonthlyProductSalesReport(warehouseId, currentDate);
        return res.stream()
                .map(GetProductSalesReportResponseDto::new)
                .collect(Collectors.toList());
    }

    @Override
    public List<GetCategorySalesReportResponseDto> getMonthlyCategorySalesReport(Long warehouseId, Date currentDate) {
        var res = orderRepository.getMonthlyCategorySalesReport(warehouseId, currentDate);
        return res.stream()
                .map(GetCategorySalesReportResponseDto::new)
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
