package com.dti.multiwarehouse.dashboard.service.impl;

import com.dti.multiwarehouse.dashboard.dto.response.GetMonthlySalesReport;
import com.dti.multiwarehouse.dashboard.service.DashboardService;
import com.dti.multiwarehouse.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class DashboardServiceImpl implements DashboardService {

    private final OrderRepository orderRepository;

    @Override
    public int getMonthlyTotalSalesReport(Long warehouseId, Date currentDate) {
        return orderRepository.getMonthlyTotalSalesReport(warehouseId, currentDate);
    }

    @Override
    public List<GetMonthlySalesReport> getMonthlyProductSalesReport(Long warehouseId, Date currentDate) {
        var res = orderRepository.getMonthlyProductSalesReport(warehouseId, currentDate);
        return res.stream()
                .map(GetMonthlySalesReport::fromDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<GetMonthlySalesReport> getMonthlyCategorySalesReport(Long warehouseId, Date currentDate) {
        var res = orderRepository.getMonthlyCategorySalesReport(warehouseId, currentDate);
        return res.stream()
                .map(GetMonthlySalesReport::fromDto)
                .collect(Collectors.toList());
    }
}
