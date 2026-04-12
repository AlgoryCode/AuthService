package com.ael.authservice.repository;

import com.ael.authservice.model.Role;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RoleRepository extends JpaRepository<Role, Integer> {
    Optional<Role> findByCode(String code);

    @EntityGraph(attributePaths = {"authorities"})
    @Query("select r from Role r where r.code = :code")
    Optional<Role> findByCodeWithAuthorities(@Param("code") String code);

    @EntityGraph(attributePaths = {"authorities"})
    @Query(
            "select r from Role r order by case when r.serviceCode is null then 0 else 1 end asc, "
                    + "r.serviceCode asc, r.code asc")
    List<Role> findAllRolesWithAuthoritiesOrdered();

    @EntityGraph(attributePaths = {"authorities"})
    @Query("select r from Role r where r.serviceCode = :serviceCode order by r.code asc")
    List<Role> findByServiceCodeWithAuthoritiesOrdered(@Param("serviceCode") String serviceCode);
}
