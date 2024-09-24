package com.dti.multiwarehouse.product.dao;

import com.dti.multiwarehouse.category.dao.Category;
import com.dti.multiwarehouse.product.dto.response.ProductDetailsResponseDto;
import com.dti.multiwarehouse.product.dto.response.ProductSummaryResponseDto;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
    private int price;

    @Column(name = "stock", nullable = false)
    private int stock;

    @Column(name = "sold", nullable = false)
    private int sold;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = true, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Category category;

    @ElementCollection(fetch = FetchType.LAZY)
    private List<String> imageUrls;

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

    public void addImageUrl(String imageUrl) {
        imageUrls.add(imageUrl);
    }

    public void removeImageUrl(String imageUrl) {
        imageUrls.remove(imageUrl);
    }

    public ProductSummaryResponseDto toProductSummaryResponseDto() {
        return ProductSummaryResponseDto.builder()
                .id(id)
                .name(name)
                .price(price)
                .stock(stock)
                .category(category.getName())
                .thumbnail(imageUrls.stream().findFirst().orElse(null))
                .build();
    }

    public HashMap<String, Object> toDocument() {
        HashMap<String, Object> document = new HashMap<>();
        document.put("id", id.toString());
        document.put("name", name);
        document.put("description", description);
        document.put("price", price);
        document.put("stock", stock);
        document.put("category", category.getName());
        document.put("sold", sold);
        document.put("thumbnail", imageUrls.stream().findFirst().orElse(null));
        return document;
    }

    public ProductDetailsResponseDto toProductDetailsResponseDto() {
        return ProductDetailsResponseDto.builder()
                .id(id)
                .name(name)
                .description(description)
                .price(price)
                .stock(stock)
                .category(category.getName())
                .imageUrls(imageUrls)
                .build();
    }
}
