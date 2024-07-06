package com.example.zajecia7doktorki.repository;

import com.example.zajecia7doktorki.domain.Action;
import com.example.zajecia7doktorki.domain.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ActionRepository extends JpaRepository<Action, Long> {
    Page<Action> findActionsByAdmin(Customer admin, Pageable pageable);

}
