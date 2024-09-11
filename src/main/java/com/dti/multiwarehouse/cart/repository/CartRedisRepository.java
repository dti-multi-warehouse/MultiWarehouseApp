package com.dti.multiwarehouse.cart.repository;

import com.dti.multiwarehouse.cart.dao.Cart;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CartRedisRepository extends CrudRepository<Cart, String> {
}
