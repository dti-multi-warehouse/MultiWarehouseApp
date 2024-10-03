package com.dti.multiwarehouse.dashboard.dto.response;

import java.time.LocalDate;

public interface RetrieveTotalSales {
    LocalDate getSaleDate();
    int getRevenue();
}
