package com.dti.multiwarehouse.order.repository;

import com.dti.multiwarehouse.dashboard.dto.response.RetrieveProductCategorySales;
import com.dti.multiwarehouse.dashboard.dto.response.RetrieveProductStockDetails;
import com.dti.multiwarehouse.dashboard.dto.response.RetrieveTotalSales;
import com.dti.multiwarehouse.order.dao.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findAllByWarehouseId(Long warehouseId);
    List<Order> findAllByUserId(Long userId);
    @Query(
            value = """
            SELECT COALESCE(SUM(o.price), 0) AS revenue
            FROM orders as o
            WHERE o.warehouse_id = :warehouseId
            AND date_trunc('month', o.created_at) = date_trunc('month', CAST(:date AS timestamp))
            AND date_trunc('year', o.created_at) = date_trunc('year', CAST(:date AS timestamp))
            AND o.status != 'CANCELLED'
            """, nativeQuery = true
    )
    int getMonthlyTotalSalesReport(@Param("warehouseId") Long warehouseId, @Param("date") LocalDate date);

    @Query(
            value = """
            SELECT created_at::date AS sale_date, SUM(price) AS revenue
            from orders
            WHERE warehouse_id = :warehouseId
            AND date_trunc('month', created_at) = date_trunc('month', CAST(:date AS timestamp))
            AND date_trunc('year', created_at) = date_trunc('year', CAST(:date AS timestamp))
            AND status != 'CANCELLED'
            GROUP BY sale_date
            ORDER BY sale_date
            """, nativeQuery = true
    )
    List<RetrieveTotalSales> getMonthlySalesReport(@Param("warehouseId") Long warehouseId, @Param("date") LocalDate date);

    @Query(
            value = """
            select p.name, sum(p.price * i.quantity) as revenue
            from orders as o
            join order_item as i on o.id = i.order_id
            join product as p on i.product_id = p.id
            where o.warehouse_id = :warehouseId
            AND date_trunc('month', o.created_at) = date_trunc('month', CAST(:date AS timestamp))
            AND date_trunc('year', o.created_at) = date_trunc('year', CAST(:date AS timestamp))
            AND o.status != 'CANCELLED'
            group by p.name
            """, nativeQuery = true
    )
    List<RetrieveProductCategorySales> getMonthlyProductSalesReport(@Param("warehouseId") Long warehouseId, @Param("date") Date date);

    @Query(
            value = """
            select c.name, sum(p.price * i.quantity) as revenue
            from orders as o
            join order_item as i on o.id = i.order_id
            join product as p on i.product_id = p.id
            join category as c on p.category_id = c.id
            where o.warehouse_id = :warehouseId
            AND date_trunc('month', o.created_at) = date_trunc('month', CAST(:date AS timestamp))
            AND date_trunc('year', o.created_at) = date_trunc('year', CAST(:date AS timestamp))
            AND o.status != 'CANCELLED'
            group by c.name
            """, nativeQuery = true
    )
    List<RetrieveProductCategorySales> getMonthlyCategorySalesReport(@Param("warehouseId") Long warehouseId, @Param("date") Date date);

    @Query(
            value = """
            SELECT oi.id, oi.quantity, o.created_at
            FROM order_item AS oi
            JOIN orders AS o ON oi.order_id = o.id
            WHERE oi.product_id = :productId
            AND o.warehouse_id = :warehouseId
            AND date_trunc('month', o.created_at) = date_trunc('month', CAST(:date AS timestamp))
            AND date_trunc('year', o.created_at) = date_trunc('year', CAST(:date AS timestamp))
            AND o.status != 'CANCELLED'
            """, nativeQuery = true
    )
    List<RetrieveProductStockDetails> getMonthlyStockOutgoing(@Param("productId") Long productId, @Param("warehouseId") Long warehouseId, @Param("date") Date date);
}
