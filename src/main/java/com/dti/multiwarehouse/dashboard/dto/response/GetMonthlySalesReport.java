package com.dti.multiwarehouse.dashboard.dto.response;

import com.dti.multiwarehouse.dashboard.dto.request.RetrieveMonthlySalesReport;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GetMonthlySalesReport {
    Long id;
    String name;
    int revenue;

    public static GetMonthlySalesReport fromDto(RetrieveMonthlySalesReport dto) {
        return GetMonthlySalesReport.builder()
                .id(dto.getId())
                .name(dto.getName())
                .revenue(dto.getRevenue())
                .build();
    }
}
