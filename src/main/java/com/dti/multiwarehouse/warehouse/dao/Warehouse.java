package com.dti.multiwarehouse.warehouse.dao;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@Entity
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
}
