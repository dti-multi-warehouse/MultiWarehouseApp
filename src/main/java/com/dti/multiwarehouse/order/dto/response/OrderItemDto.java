package com.dti.multiwarehouse.order.dto.response;

import com.dti.multiwarehouse.order.dao.OrderItem;
import lombok.Getter;

@Getter
public class OrderItemDto {
    private final Long id;
    private final String thumbnail;
    private final String name;
    private final int quantity;

    public OrderItemDto(OrderItem orderItem) {
        this.id = orderItem.getId();
        this.thumbnail = orderItem.getProduct().getImageUrls().getFirst();
        this.name = orderItem.getProduct().getName();
        this.quantity = orderItem.getQuantity();
    }
}
