package com.dti.multiwarehouse.product.dao;

import com.dti.multiwarehouse.product.dto.response.ProductSummaryResponseDto;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.HashMap;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "product")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "product_id_gen")
    @SequenceGenerator(name = "product_id_gen", sequenceName = "product_id_seq", allocationSize = 1)
    private Long id;

    @Column(name = "name", nullable = false, unique = true)
    private String name;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "price", nullable = false)
    private BigDecimal price;

    @Column(name = "stock", nullable = false)
    private int stock;

    @Column(name = "sold", nullable = false)
    private int sold;

    @Column(name = "category_id", nullable = false)
    private Long categoryId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", insertable = false, updatable = false)
    private Category category;

    @CreationTimestamp
    @Column(name = "createdAt")
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updatedAt")
    private Instant updatedAt;

    @Column(name = "deletedAt")
    private Instant deletedAt;

    @Column(name = "archivedAt")
    private Instant archivedAt;

}
