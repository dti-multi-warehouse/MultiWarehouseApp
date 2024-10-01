package com.dti.multiwarehouse.dashboard.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GetProductCategorySalesReportResponseDto {
    String name;
    int revenue;

    public static GetProductCategorySalesReportResponseDto fromDto(RetrieveProductCategorySales dto) {
        return GetProductCategorySalesReportResponseDto.builder()
                .name(dto.getName())
                .revenue(dto.getRevenue())
                .build();
    }
}
