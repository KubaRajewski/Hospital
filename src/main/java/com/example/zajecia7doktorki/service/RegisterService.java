package com.example.zajecia7doktorki.service;

import com.example.zajecia7doktorki.domain.Admin;
import com.example.zajecia7doktorki.domain.Customer;
import com.example.zajecia7doktorki.domain.Doctor;
import com.example.zajecia7doktorki.domain.Patient;
import com.example.zajecia7doktorki.exception.LoginAlreadyExistsException;
import com.example.zajecia7doktorki.exception.PeselAlreadyExistsException;
import com.example.zajecia7doktorki.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import static com.example.zajecia7doktorki.model.Role.ADMIN;
import static com.example.zajecia7doktorki.model.Role.USER;

@Service
@RequiredArgsConstructor
public class RegisterService {

    private final CustomerRepository customerRepository;

    private final ValidationService validationService;

    private final BCryptPasswordEncoder passwordEncoder;

    public Patient createPatient(Customer customer) {
        customer.setPassword(passwordEncoder.encode(customer.getPassword()));
        validate(customer);
        customer.setRole(USER);

        return (Patient) customerRepository.save(customer);
    }

    public Doctor createDoctor(Customer customer) {
        customer.setPassword(passwordEncoder.encode(customer.getPassword()));
        validate(customer);
        customer.setRole(USER);

        return (Doctor) customerRepository.save(customer);
    }

    public Admin createAdmin(Customer customer) {
        customer.setPassword(passwordEncoder.encode(customer.getPassword()));
        validate(customer);
        customer.setRole(ADMIN);

        return (Admin) customerRepository.save(customer);
    }

    private void validate(Customer customer) {
        if (validationService.isLoginInDatabase(customer.getLogin())) {
            throw new LoginAlreadyExistsException("User with this login already exists");
        } else if (validationService.isPeselInDatabase(customer.getPesel())) {
            throw new PeselAlreadyExistsException("User with this pesel already exists.");
        }
    }
}
