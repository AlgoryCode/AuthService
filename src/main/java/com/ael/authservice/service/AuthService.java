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

}
