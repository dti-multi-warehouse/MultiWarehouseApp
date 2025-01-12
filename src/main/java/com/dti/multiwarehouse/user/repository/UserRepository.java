package com.dti.multiwarehouse.user.repository;

import com.dti.multiwarehouse.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    Optional<User> findById(Long id);
    List<User> findByRole(String role);
    @Query("SELECT u FROM User u WHERE " +
            "(:role IS NULL OR LOWER(u.role) = LOWER(CAST(:role AS string))) AND " +
            "((:username IS NOT NULL AND LOWER(u.username) LIKE LOWER(CONCAT('%', CAST(:username AS string), '%'))) OR " +
            "(:email IS NOT NULL AND LOWER(u.email) LIKE LOWER(CONCAT('%', CAST(:email AS string), '%'))) OR " +
            "(:username IS NULL AND :email IS NULL))")
    Page<User> findAllByUsernameEmailAndRole(
            @Param("role") String role,
            @Param("username") String username,
            @Param("email") String email,
            Pageable pageable
    );
}
