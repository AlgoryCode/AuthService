package com.ael.authservice.service;


import com.ael.authservice.model.User;
import com.ael.authservice.repository.IAuthRepository;
import com.ael.authservice.repository.ICustomerService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
public class AuthService {

    private final ICustomerService customerService;
    private final IAuthRepository authRepository;

    public User createClient(User user) {
        return customerService.save(user);
    }

    public Optional<User> getClient(String username, String password) {
        return Optional.ofNullable(customerService.findByUsernameAndPassword(username, password));
    }

    public Optional<User> getClientById(Long customerId) {
        return customerService.findById(customerId);
    }

}
