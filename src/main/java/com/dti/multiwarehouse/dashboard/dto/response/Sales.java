package com.dti.multiwarehouse.dashboard.dto.response;

import lombok.Data;

import java.time.LocalDate;

@Data
public class Sales {
    LocalDate saleDate;
    int revenue;

    public Sales(RetrieveTotalSales dto) {
        this.saleDate = dto.getSaleDate();
        this.revenue = dto.getRevenue();
    }

    public static Sales fromDto(RetrieveTotalSales dto) {
        return new Sales(dto);
    }
}
