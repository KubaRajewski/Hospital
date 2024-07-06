package com.example.zajecia7doktorki.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class LoginNotFoundException extends RuntimeException {
    public LoginNotFoundException() {
    }

    public LoginNotFoundException(String message) {
        super(message);
    }
}
