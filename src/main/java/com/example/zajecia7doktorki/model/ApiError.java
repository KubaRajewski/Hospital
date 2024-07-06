package com.example.zajecia7doktorki.model;

import lombok.*;
import org.springframework.http.HttpStatus;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApiError {
    private HttpStatus httpStatus;
    private Object errors;
}
