package com.dti.multiwarehouse.order.repository;

import com.dti.multiwarehouse.dashboard.dto.request.RetrieveMonthlySalesReport;
import com.dti.multiwarehouse.order.dao.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    @Query(
            value = """
            SELECT COALESCE(SUM(o.price), 0) AS revenue
            FROM orders as o
            WHERE o.warehouse_id = ? AND date_trunc('month', o.created_at) = date_trunc('month', ?::timestamp)
            """, nativeQuery = true
    )
    int getMonthlyTotalSalesReport(@Param("warehouseId") Long warehouseId, @Param("currentDate") Date currentDate);

    @Query(
            value = """
             SELECT p.id, p.name, sum(p.price * i.quantity) AS revenue
             FROM orders AS o
             JOIN order_item AS i ON o.id = i.order_id
             JOIN product AS p ON i.product_id = p.id
             WHERE o.warehouse_id = ? AND date_trunc('month', o.created_at) = date_trunc('month', ?::timestamp)
             GROUP BY p.id
            """, nativeQuery = true
    )
    List<RetrieveMonthlySalesReport> getMonthlyProductSalesReport(@Param("warehouseId") Long warehouseId, @Param("currentDate") Date currentDate);

    @Query(
            value = """
            SELECT c.id, c.name, sum(p.price * i.quantity) AS revenue
            FROM orders AS o
            JOIN order_item AS i ON o.id = i.order_id
            JOIN product AS p ON i.product_id = p.id
            JOIN category AS c ON p.category_id = c.id
            WHERE o.warehouse_id = ? AND date_trunc('month', o.created_at) = date_trunc('month', ?::timestamp)
            GROUP BY c.id
            """, nativeQuery = true
    )
    List<RetrieveMonthlySalesReport> getMonthlyCategorySalesReport(@Param("warehouseId") Long warehouseId, @Param("currentDate") Date currentDate);
}
