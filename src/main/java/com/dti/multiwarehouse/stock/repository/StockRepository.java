package com.dti.multiwarehouse.stock.repository;

import com.dti.multiwarehouse.stock.dao.Stock;
import com.dti.multiwarehouse.stock.dao.key.StockCompositeKey;
import com.dti.multiwarehouse.stock.dto.response.RetrieveProductAndStockAvailabilityDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface StockRepository extends JpaRepository<Stock, Long> {
    Optional<Stock> findById(StockCompositeKey id);
    @Query(
            value = """
            SELECT p.id, p.name, COALESCE(s.stock, 0) AS stock,
                   (SELECT i.image_urls
                    FROM product_image_urls i
                    WHERE i.product_id = p.id
                    LIMIT 1) AS thumbnail
            FROM product p
            LEFT JOIN stock s ON p.id = s.product_id AND s.warehouse_id = :warehouseId
            ORDER BY p.id;
            """, nativeQuery = true
    )
    List<RetrieveProductAndStockAvailabilityDto> retrieveProductAndStockAvailability(@Param("warehouseId") Long warehouseId);

}
