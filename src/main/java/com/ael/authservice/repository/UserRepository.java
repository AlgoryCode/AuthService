package com.ael.authservice.repository;

import com.ael.authservice.model.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    @Query(
            "SELECT DISTINCT u FROM User u LEFT JOIN FETCH u.role r LEFT JOIN FETCH r.authorities WHERE u.email = :email")
    Optional<User> findByEmailWithRoleAndAuthorities(@Param("email") String email);

    @Query(
            "SELECT DISTINCT u FROM User u LEFT JOIN FETCH u.role r LEFT JOIN FETCH r.authorities WHERE u.userId = :id")
    Optional<User> findByIdWithRoleAndAuthorities(@Param("id") Integer id);
}
