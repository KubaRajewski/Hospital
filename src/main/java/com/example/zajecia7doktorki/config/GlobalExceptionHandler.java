package com.example.zajecia7doktorki.config;

import com.example.zajecia7doktorki.exception.*;
import com.example.zajecia7doktorki.model.ApiError;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.Map;
import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                  @NonNull HttpHeaders headers,
                                                                  @NonNull HttpStatus status,
                                                                  @NonNull WebRequest request) {
        Map<String, String> errorDetails = ex.getBindingResult().getFieldErrors().stream()
                .collect(Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage));

        ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST, errorDetails);
        logger.error(ex.getMessage());
        return new ResponseEntity<>(apiError, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = {
            AppointmentNotFoundException.class,
            PatientNotFoundException.class,
            DoctorNotFoundException.class,
            LoginNotFoundException.class,
            AdminNotFoundException.class
    })
    protected ResponseEntity<ApiError> handleNotFoundException(RuntimeException ex) {
        ApiError apiError = new ApiError(HttpStatus.NOT_FOUND, ex.getMessage());
        logger.error(ex.getMessage());
        return new ResponseEntity<>(apiError, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = {
            PeselAlreadyExistsException.class,
            LoginAlreadyExistsException.class,
            AppointmentConflictException.class,
            DoctorAlreadyExistsException.class,
            PatientAlreadyExistsException.class
    })
    protected ResponseEntity<ApiError> handleDuplicateEntityException(RuntimeException ex) {
        ApiError apiError = new ApiError(HttpStatus.CONFLICT, ex.getMessage());
        logger.error(ex.getMessage());
        return new ResponseEntity<>(apiError, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(PermissionDeniedException.class)
    protected ResponseEntity<ApiError> handlePermissionDeniedException(RuntimeException ex) {
        ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST, ex.getMessage());
        logger.error(ex.getMessage());
        return new ResponseEntity<>(apiError, HttpStatus.BAD_REQUEST);
    }
}
