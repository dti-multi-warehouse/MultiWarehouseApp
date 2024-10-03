package com.dti.multiwarehouse.dashboard.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
public class GetTotalSalesResponseDto {
    int totalRevenue;
    List<Sales> sales;
}
