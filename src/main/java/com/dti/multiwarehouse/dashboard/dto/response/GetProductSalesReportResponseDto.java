package com.dti.multiwarehouse.dashboard.dto.response;

import lombok.Getter;

@Getter
public class GetProductSalesReportResponseDto {
    private final String name;
    private final int revenue;

    public GetProductSalesReportResponseDto(RetrieveProductCategorySales dto) {
        this.name = dto.getName();
        this.revenue = dto.getRevenue();
    }
}
