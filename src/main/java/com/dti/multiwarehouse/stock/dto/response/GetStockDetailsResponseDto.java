package com.dti.multiwarehouse.stock.dto.response;

import lombok.Data;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.WeekFields;
import java.util.*;

@Data
public class GetStockDetailsResponseDto {
    private List<StockMovement> stockMovements;
    private List<StockMovementChartData> stockMovementChartData;

    public GetStockDetailsResponseDto(List<StockMovement> stockMovements) {
        this.stockMovements = stockMovements;
        createChartData();
    }

    public void createChartData() {
        var chartData = new HashMap<Integer, StockMovementChartData>();
        var locale = ZoneId.of("Asia/Jakarta");
        var weekFields = WeekFields.of(Locale.forLanguageTag("id-ID"));

        for (var stockMovement : stockMovements) {
            var date = LocalDate.ofInstant(stockMovement.getDate(), locale);
            var week = date.get(weekFields.weekOfMonth());

            var data = chartData.computeIfAbsent(week, k -> {
                var newData = new StockMovementChartData();
                newData.setPeriod(week);
                return newData;
            });

            switch (stockMovement.getSource()) {
                case "restock":
                    data.setRestock(data.getRestock() + stockMovement.getQuantity());
                    break;
                case "mutation_in":
                    data.setMutationIn(data.getMutationIn() + stockMovement.getQuantity());
                    break;
                case "mutation_out":
                    data.setMutationOut(data.getMutationOut() + stockMovement.getQuantity());
                    break;
                case "order":
                    data.setOrder(data.getOrder() + stockMovement.getQuantity());
                    break;
            }
        }

        this.stockMovementChartData = new ArrayList<>(chartData.values());
    }
}