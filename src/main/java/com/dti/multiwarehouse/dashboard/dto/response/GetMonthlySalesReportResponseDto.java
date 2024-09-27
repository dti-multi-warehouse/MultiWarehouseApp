package com.dti.multiwarehouse.dashboard.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GetMonthlySalesReportResponseDto {
    Long id;
    String name;
    int revenue;

    public static GetMonthlySalesReportResponseDto fromDto(RetrieveMonthlySalesReport dto) {
        return GetMonthlySalesReportResponseDto.builder()
                .id(dto.getId())
                .name(dto.getName())
                .revenue(dto.getRevenue())
                .build();
    }
}
