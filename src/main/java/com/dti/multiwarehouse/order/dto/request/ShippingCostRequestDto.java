package com.dti.multiwarehouse.order.dto.request;

import lombok.Data;

@Data
public class ShippingCostRequestDto {
    private Long originCityId;
    private Long destinationCityId;
    private int weight;
    private String courier;
}
