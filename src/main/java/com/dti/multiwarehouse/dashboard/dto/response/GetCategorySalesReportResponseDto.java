package com.dti.multiwarehouse.dashboard.dto.response;

import lombok.Getter;

@Getter
public class GetCategorySalesReportResponseDto {
    public final String name;
    public final int revenue;
    public final String fill;

    public GetCategorySalesReportResponseDto(RetrieveProductCategorySales dto) {
        this.name = dto.getName();
        this.revenue = dto.getRevenue();
        this.fill = String.format("var(--color-%s)", dto.getName());
    }

}
