package com.dti.multiwarehouse.stock.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GetProductAndStockAvailabilityDto {
    private Long productId;
    private String name;
    private int stock;
    private String thumbnail;

    public static GetProductAndStockAvailabilityDto fromDto(RetrieveProductAndStockAvailabilityDto dto) {
        return GetProductAndStockAvailabilityDto.builder()
                .productId(dto.getId())
                .name(dto.getName())
                .stock(dto.getStock())
                .thumbnail(dto.getThumbnail())
                .build();
    }
}
