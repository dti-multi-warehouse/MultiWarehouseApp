package com.dti.multiwarehouse.dashboard.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;


import java.util.List;

@Getter
@AllArgsConstructor
public class GetTotalSalesResponseDto {
    public final int totalRevenue;
    public final List<Sales> sales;
}
