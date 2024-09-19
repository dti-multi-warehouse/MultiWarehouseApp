package com.dti.multiwarehouse.warehouse.dao;

import com.dti.multiwarehouse.address.entity.WarehouseAddress;
import com.dti.multiwarehouse.stock.dao.Stock;
import com.dti.multiwarehouse.user.entity.WarehouseAdmin;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "warehouse")
public class Warehouse {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "warehouse_id_gen")
    @SequenceGenerator(name = "warehouse_id_gen", sequenceName = "warehouse_id_seq", allocationSize = 1)
    private Long id;

    @CreationTimestamp
    @Column(name = "createdAt")
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updatedAt")
    private Instant updatedAt;

    @Column(name = "deletedAt")
    private Instant deletedAt;

    @OneToOne(mappedBy = "warehouse", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private WarehouseAddress warehouseAddress;

    @OneToMany(mappedBy = "warehouse", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<WarehouseAdmin> warehouseAdmins;

    @OneToMany(mappedBy = "warehouse", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Stock> stocks;
}
