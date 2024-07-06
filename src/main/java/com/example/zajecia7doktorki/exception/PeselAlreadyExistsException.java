package com.example.zajecia7doktorki.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.CONFLICT)
public class PeselAlreadyExistsException extends RuntimeException {
    public PeselAlreadyExistsException(String message) {
        super(message);
    }
}
