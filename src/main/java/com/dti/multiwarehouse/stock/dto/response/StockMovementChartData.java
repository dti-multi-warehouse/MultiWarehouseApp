package com.dti.multiwarehouse.stock.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StockMovementChartData {
    private int period;
    private int restock;
    private int mutationIn;
    private int mutationOut;
    private int order;

}
