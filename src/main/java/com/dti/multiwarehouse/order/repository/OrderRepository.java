package com.dti.multiwarehouse.order.repository;

import com.dti.multiwarehouse.order.dao.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
}
