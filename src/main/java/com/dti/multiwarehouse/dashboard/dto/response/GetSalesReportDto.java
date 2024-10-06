package com.dti.multiwarehouse.dashboard.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class GetSalesReportDto {
    private final GetTotalSalesResponseDto totalSales;
    private final List<GetProductSalesReportResponseDto> productSales;
    private final List<GetCategorySalesReportResponseDto> categorySales;
}
