package com.dti.multiwarehouse.dashboard.dto.response;

import lombok.Data;

@Data
public class GetProductSalesReportResponseDto {
    private String name;
    private int revenue;

    public GetProductSalesReportResponseDto(RetrieveProductCategorySales dto) {
        this.name = dto.getName();
        this.revenue = dto.getRevenue();
    }
}
