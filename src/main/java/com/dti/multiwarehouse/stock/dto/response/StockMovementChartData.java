package com.dti.multiwarehouse.stock.dto.response;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class StockMovementChartData {
    private int period;
    private int restock;
    private int mutationIn;
    private int mutationOut;
    private int order;

}
