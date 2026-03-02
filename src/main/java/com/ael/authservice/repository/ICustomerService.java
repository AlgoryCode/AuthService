package com.ael.authservice.repository;

import com.ael.authservice.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ICustomerService extends JpaRepository<User,Long> {

    User findByUsernameAndPassword(String username, String password);
}
