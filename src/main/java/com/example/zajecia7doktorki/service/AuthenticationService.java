package com.example.zajecia7doktorki.service;


import com.example.zajecia7doktorki.config.security.jwt.JwtService;
import com.example.zajecia7doktorki.domain.Customer;
import com.example.zajecia7doktorki.exception.LoginNotFoundException;
import com.example.zajecia7doktorki.model.AuthenticationRequest;
import com.example.zajecia7doktorki.model.AuthenticationResponse;
import com.example.zajecia7doktorki.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final CustomerRepository customerRepository;

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        Customer customer = customerRepository.findByLogin(request.getLogin())
                .orElseThrow(LoginNotFoundException::new);
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getLogin(), request.getPassword()));
        String jwtToken = jwtService.generateToken(customer);
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();
    }
}