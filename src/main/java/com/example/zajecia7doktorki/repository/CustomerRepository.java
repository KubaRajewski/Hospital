package com.example.zajecia7doktorki.repository;

import com.example.zajecia7doktorki.domain.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer, Long> {

    Optional<Customer> findByIdAndUserType(Long id, String userType);

    Page<Customer> findAllByUserType(String userType, Pageable pageable);

    Optional<Customer> findByLogin(String login);

    Optional<Customer> findByPesel(String pesel);

}
