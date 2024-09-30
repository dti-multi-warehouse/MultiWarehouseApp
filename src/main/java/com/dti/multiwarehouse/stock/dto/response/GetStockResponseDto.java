package com.dti.multiwarehouse.stock.dto.response;

import com.dti.multiwarehouse.stock.dao.Stock;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GetStockResponseDto {
    private Long id;
    private String name;
    private int stock;
    private String thumbnail;
    private int incoming;
    private int outgoing;

    public static GetStockResponseDto fromDto(RetrieveStock dto) {
        return GetStockResponseDto.builder()
                .id(dto.getId())
                .name(dto.getName())
                .stock(dto.getStock())
                .thumbnail(dto.getThumbnail())
                .incoming(dto.getIncoming())
                .outgoing(dto.getOutgoing())
                .build();
    }
}
