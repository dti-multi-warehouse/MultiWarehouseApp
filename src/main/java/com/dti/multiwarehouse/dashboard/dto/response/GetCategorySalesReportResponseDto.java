package com.dti.multiwarehouse.dashboard.dto.response;

import lombok.Data;

@Data
public class GetCategorySalesReportResponseDto {
    String name;
    int revenue;
    String fill;

    public GetCategorySalesReportResponseDto(RetrieveProductCategorySales dto) {
        this.name = dto.getName();
        this.revenue = dto.getRevenue();
        this.fill = String.format("var(--color-%s)", dto.getName());
    }

}
