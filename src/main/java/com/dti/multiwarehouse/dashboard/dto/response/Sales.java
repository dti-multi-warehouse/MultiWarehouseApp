package com.dti.multiwarehouse.dashboard.dto.response;

import lombok.Getter;

import java.time.LocalDate;

@Getter
public class Sales {
    LocalDate saleDate;
    int revenue;

    public Sales(RetrieveTotalSales dto) {
        this.saleDate = dto.getSaleDate();
        this.revenue = dto.getRevenue();
    }
}
