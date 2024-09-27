package com.dti.multiwarehouse.warehouse.mapper;

import com.dti.multiwarehouse.warehouse.dao.Warehouse;
import com.dti.multiwarehouse.warehouse.dto.WarehouseDTO;
import org.springframework.stereotype.Component;

@Component
public class WarehouseMapper {

    public WarehouseDTO toDTO(Warehouse warehouse) {
        WarehouseDTO dto = new WarehouseDTO();
        dto.setId(warehouse.getId());
        dto.setName(warehouse.getName());
        dto.setStreet(warehouse.getWarehouseAddress().getAddress().getStreet());
        dto.setCity(warehouse.getWarehouseAddress().getAddress().getCity());
        dto.setProvince(warehouse.getWarehouseAddress().getAddress().getProvince());
        dto.setLatitude(warehouse.getWarehouseAddress().getAddress().getLatitude());
        dto.setLongitude(warehouse.getWarehouseAddress().getAddress().getLongitude());
        dto.setAdminUsername(warehouse.getWarehouseAdmins().get(0).getUser().getUsername()); // Example for the first admin
        return dto;
    }
}