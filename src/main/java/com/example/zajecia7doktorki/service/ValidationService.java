package com.example.zajecia7doktorki.service;

import com.example.zajecia7doktorki.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ValidationService {

    private final CustomerRepository customerRepository;

    protected boolean isLoginInDatabase(String login) {
        return customerRepository.findByLogin(login).isPresent();
    }

    protected boolean isPeselInDatabase(String pesel) {
        return customerRepository.findByPesel(pesel).isPresent();
    }
}
