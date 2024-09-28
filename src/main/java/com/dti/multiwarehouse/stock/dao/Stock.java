package com.dti.multiwarehouse.stock.dao;

import com.dti.multiwarehouse.stock.dao.key.StockCompositeKey;
import com.dti.multiwarehouse.warehouse.dao.Warehouse;
import jakarta.persistence.*;
import com.dti.multiwarehouse.stock.dto.response.GetStockResponseDto;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.proxy.HibernateProxy;

import java.time.Instant;
import java.util.Objects;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Entity
public class Stock {

    @EmbeddedId
    private StockCompositeKey id;

    private int stock;

    @MapsId("warehouse")
    @ManyToOne
    @JoinColumn(name = "warehouse_id")
    private Warehouse warehouse;

    @CreationTimestamp
    @Column(name = "createdAt")
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updatedAt")
    private Instant updatedAt;

    @Column(name = "deletedAt")
    private Instant deletedAt;

    public Stock(StockCompositeKey id, int stock) {
        this.id = id;
        this.stock = stock;
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        Stock stock = (Stock) o;
        return getId() != null && Objects.equals(getId(), stock.getId());
    }

    @Override
    public final int hashCode() {
        return Objects.hash(id);
    }
}