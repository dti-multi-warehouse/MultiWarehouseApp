package com.dti.multiwarehouse.order.dto.response;

import com.dti.multiwarehouse.address.entity.Address;
import com.dti.multiwarehouse.address.entity.UserAddress;
import com.dti.multiwarehouse.address.entity.WarehouseAddress;
import com.dti.multiwarehouse.order.dao.Order;
import com.dti.multiwarehouse.order.dao.OrderItem;
import com.dti.multiwarehouse.warehouse.dao.Warehouse;
import lombok.Data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
@Data
public class OrderDetailsResponseDto {
    private Long id;
    private Long userId;
    private String userName;
    private Long warehouseId;
    private String warehouseName;
    private Integer price;
    private String paymentProof;
    private String status;
    private String paymentMethod;
    private Integer shippingCost;
    private String bank;
    private String accountNumber;
    private String createdAt;
    private String paymentExpiredAt;
    private Long invoiceNumber;
    private List<OrderItemDto> items; // Change to a list of OrderItemDto
    private Integer totalAmount;
    private String buyerName;
    private String buyerPhoneNumber;
    private Address buyerAddress;
    private Address warehouseAddress;
    private String statusLabel;
    private String shippingDate;
    private String virtualAccountNumber;

    public OrderDetailsResponseDto(Order order) {
        this.id = order.getId();
        this.userId = order.getUser() != null ? order.getUser().getId() : null;
        this.userName = order.getUser() != null ? order.getUser().getUsername() : null;
        this.warehouseId = order.getWarehouse() != null ? order.getWarehouse().getId() : null;
        this.warehouseName = order.getWarehouse() != null ? order.getWarehouse().getName() : null;
        this.paymentProof = order.getPaymentProof();
        this.status = order.getStatus() != null ? order.getStatus().toString() : null;
        this.paymentMethod = order.getPaymentMethod() != null ? order.getPaymentMethod().toString() : null;
        this.shippingCost = order.getShippingCost();
        this.bank = order.getBank() != null ? order.getBank().toString() : null;
        this.accountNumber = order.getAccountNumber();
        this.createdAt = order.getCreatedAt() != null ? order.getCreatedAt().toString() : null;
        this.paymentExpiredAt = order.getPaymentExpiredAt() != null ? order.getPaymentExpiredAt().toString() : null;
        this.invoiceNumber = order.getId();
        this.price = order.getPrice();

        setOrderItemDetails(order.getOrderItems());
        setUserAddressDetails(order.getShippingAddress());
        setWarehouseAddressDetails(order.getWarehouse());

        this.totalAmount = (order.getPrice() != 0 && order.getShippingCost() != 0)
                ? order.getPrice() + order.getShippingCost()
                : null;

        this.statusLabel = this.status;
        this.shippingDate = this.createdAt;
        this.virtualAccountNumber = this.accountNumber;
    }

    private void setOrderItemDetails(Collection<?> orderItems) {
        this.items = new ArrayList<>();
        if (orderItems != null && !orderItems.isEmpty()) {
            for (Object itemObject : orderItems) {
                if (itemObject instanceof OrderItem) {
                    OrderItem item = (OrderItem) itemObject;
                    this.items.add(new OrderItemDto(item));
                }
            }
        }
    }

    private void setUserAddressDetails(UserAddress buyerAddress) {
        if (buyerAddress != null) {
            this.buyerName = buyerAddress.getName();
            this.buyerPhoneNumber = buyerAddress.getPhoneNumber();
            this.buyerAddress = buyerAddress.getAddress();
        }
    }

    private void setWarehouseAddressDetails(Warehouse warehouse) {
        if (warehouse != null) {
            WarehouseAddress warehouseAddress = warehouse.getWarehouseAddress();
            if (warehouseAddress != null) {
                this.warehouseAddress = warehouseAddress.getAddress();
            }
        }
    }
}