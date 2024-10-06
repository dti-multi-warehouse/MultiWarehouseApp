package com.dti.multiwarehouse.category.dao;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.util.HashMap;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "category")
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "category_id_gen")
    @SequenceGenerator(name = "category_id_gen", sequenceName = "category_id_seq", allocationSize = 1)
    private Long id;

    @Column(name= "name", nullable = false)
    private String name;

    @Column(name = "logoUrl", nullable = false)
    private String logoUrl;

    @CreationTimestamp
    @Column(name = "createdAt")
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updatedAt")
    private Instant updatedAt;

    @Column(name = "deletedAt")
    private Instant deletedAt;

    public HashMap<String, Object> toDocument() {
        HashMap<String, Object> document = new HashMap<>();
        document.put("id", id.toString());
        document.put("name",name);
        document.put("logoUrl",logoUrl);
        return document;
    }
}
