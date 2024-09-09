package com.dti.multiwarehouse.stock.dao.key;

import com.dti.multiwarehouse.product.dao.Product;
import com.dti.multiwarehouse.warehouse.dao.Warehouse;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StockCompositeKey implements Serializable {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false, updatable = false)
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "warehouse_id", nullable = false, updatable = false)
    private Warehouse warehouse;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StockCompositeKey that = (StockCompositeKey) o;
        return Objects.equals(product, that.product) && Objects.equals(warehouse, that.warehouse);
    }

    @Override
    public int hashCode() {
        return Objects.hash(product, warehouse);
    }
}
