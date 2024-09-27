package com.dti.multiwarehouse.warehouse.dto;

import lombok.Data;

@Data
public class WarehouseDTO {
    private Long id;
    private String name;
    private String street;
    private String city;
    private String province;
    private Double latitude;
    private Double longitude;
    private String adminUsername;
}
