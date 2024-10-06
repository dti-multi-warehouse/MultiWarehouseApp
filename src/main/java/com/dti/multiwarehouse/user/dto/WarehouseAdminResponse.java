package com.dti.multiwarehouse.user.dto;

import lombok.Data;

@Data
public class WarehouseAdminResponse {
    private Long id;
    private String username;
    private String email;
    private String avatar;
    private Long warehouseId;
    private String warehouseName;
}