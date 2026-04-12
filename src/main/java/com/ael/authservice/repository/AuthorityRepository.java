package com.ael.authservice.repository;

import com.ael.authservice.model.Authority;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthorityRepository extends JpaRepository<Authority, Integer> {
    Optional<Authority> findByCode(String code);

    List<Authority> findAllByOrderByCodeAsc();
}
