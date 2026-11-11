package com.atlas.bank.atlas_bank.application.port.out;

import com.atlas.bank.atlas_bank.domain.model.customer.Customer;

import java.util.Optional;

public interface CustomerRepositoryPort {
    Optional<Customer> findById(Long id);
    Optional<Customer> findByKeycloakSubject(String keycloakSubject);
    Customer save(Customer customer);
}
