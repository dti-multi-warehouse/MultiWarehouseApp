package com.dti.multiwarehouse.warehouse.dto;

import com.dti.multiwarehouse.warehouse.dao.Warehouse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WarehouseListResponseDto {
    private Long id;
    private String name;

    public static WarehouseListResponseDto fromEntity(Warehouse warehouse) {
        return WarehouseListResponseDto.builder()
                .id(warehouse.getId())
                .name(warehouse.getName())
                .build();
    }
}
