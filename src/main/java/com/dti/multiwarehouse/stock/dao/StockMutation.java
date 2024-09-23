package com.dti.multiwarehouse.stock.dao;

import com.dti.multiwarehouse.product.dao.Product;
import com.dti.multiwarehouse.stock.dao.enums.StockMutStatus;
import com.dti.multiwarehouse.stock.dto.response.StockMutationRequestResponseDto;
import com.dti.multiwarehouse.warehouse.dao.Warehouse;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.proxy.HibernateProxy;

import java.time.Instant;
import java.util.Objects;

@Getter
@Setter
@ToString
@AllArgsConstructor
@Builder
@Entity
@NoArgsConstructor
public class StockMutation {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "stock_mut_id_gen")
    @SequenceGenerator(name = "stock_mut_id_gen", sequenceName = "stock_mut_id_seq", allocationSize = 1)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "warehouse_from_id", nullable = true)
    @ToString.Exclude
    private Warehouse warehouseFrom;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "warehouse_to_id", nullable = false)
    @ToString.Exclude
    private Warehouse warehouseTo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    @ToString.Exclude
    private Product product;

    @Column(name = "quantity")
    private int quantity;

    @Enumerated(EnumType.STRING)
    private StockMutStatus status;

    @CreationTimestamp
    @Column(name = "createdAt")
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updatedAt")
    private Instant updatedAt;

    @Column(name = "deletedAt")
    private Instant deletedAt;

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        StockMutation that = (StockMutation) o;
        return getId() != null && Objects.equals(getId(), that.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }

    public StockMutationRequestResponseDto toStockMutationRequestResponseDto() {
        return StockMutationRequestResponseDto.builder()
                .id(id)
                .warehouseFromId(warehouseFrom.getId())
                .warehouseToId(warehouseTo.getId())
                .quantity(quantity)
                .productName(product.getName())
                .thumbnail(product.getImageUrls().getFirst())
                .createdAt(createdAt)
                .build();
    }
}
