package com.dti.multiwarehouse.user.service;

import com.dti.multiwarehouse.user.entity.User;

import java.util.Optional;

public interface UserService {
    Optional<User> findByEmail(String Email);
    User save(User user);
}
