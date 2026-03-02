package com.ael.authservice.client;


import com.ael.authservice.dto.response.CustomerResponse;
import com.ael.authservice.model.User;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@FeignClient(name="CustomerService")
public interface ICustomerClient {

    @PostMapping("/customer/createCustomer")
    ResponseEntity<User> createCustomer(@RequestBody User user);

    @GetMapping("/customer/getCustomer/{customerId}")
    Optional<User> getCustomer(@PathVariable("customerId") Integer customerId);

    @PostMapping("/customer/authenticateCustomer")
    Optional<CustomerResponse> authenticateCustomer(@RequestParam String username, @RequestParam String password);

    @GetMapping("/customer/getAllCustomerInfoByCustomerId/{customerId}")
    ResponseEntity<CustomerResponse> getAllCustomerInfo(@PathVariable Integer customerId);
    
    @GetMapping("/customer/getCustomerProfile/{customerId}")
    ResponseEntity<Object> getCustomerProfile(@PathVariable Integer customerId);
}
