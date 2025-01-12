package com.dti.multiwarehouse.warehouse.repository;

import com.dti.multiwarehouse.warehouse.dao.Warehouse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface WarehouseRepository extends JpaRepository<Warehouse, Long> {
    @Query("SELECT w FROM Warehouse w " +
            "JOIN w.warehouseAddress wa " +
            "JOIN wa.address a " +
            "WHERE " +
            "(:name IS NOT NULL AND LOWER(CAST(w.name AS string)) LIKE LOWER(CONCAT('%', CAST(:name AS string), '%'))) OR " +
            "(:city IS NOT NULL AND LOWER(CAST(a.city AS string)) LIKE LOWER(CONCAT('%', CAST(:city AS string), '%'))) OR " +
            "(:province IS NOT NULL AND LOWER(CAST(a.province AS string)) LIKE LOWER(CONCAT('%', CAST(:province AS string), '%'))) OR " +
            "(:name IS NULL AND :city IS NULL AND :province IS NULL)")
    Page<Warehouse> searchWarehouses(
            @Param("name") String name,
            @Param("city") String city,
            @Param("province") String province,
            Pageable pageable
    );

    @Query(
            value = """
            SELECT *
            FROM warehouse
            LIMIT 1
            """, nativeQuery = true
    )
    Warehouse findFirstWarehouse();

    @Query(
            value = """
            SELECT w.*, 
                ST_DistanceSphere(ST_MakePoint(a.longitude, a.latitude), 
                ST_MakePoint(:longitude, :latitude)) as distance
            FROM warehouse w
            JOIN warehouse_address wa on w.id = wa.warehouse_id
            JOIN address a on a.id = wa.address_id
            WHERE w.id != :warehouseId
            ORDER BY distance
            """, nativeQuery = true
    )
    List<Warehouse> findNearbyWarehouses(@Param("warehouseId") Long warehouseId,@Param("longitude") double longitude,@Param("latitude") double latitude);
}
