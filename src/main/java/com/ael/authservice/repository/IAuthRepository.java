package com.ael.authservice.repository;


import com.ael.authservice.model.TokenLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IAuthRepository extends JpaRepository<TokenLog,Long> {

}
