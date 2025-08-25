package com.rental.rental_management_api.controller;

import com.rental.rental_management_api.exception.ParentHasChildException;
import com.rental.rental_management_api.exception.ResourceNotFoundException;
import com.rental.rental_management_api.payload.ErrorDetails;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    private ResponseEntity<ErrorDetails> buildErrorResponse(
            Exception ex,
            String message,
            String description,
            HttpStatus status
    ) {
        return buildErrorResponse(ex, message, description, status, false);
    }
    private ResponseEntity<ErrorDetails> buildErrorResponse(
            Exception ex,
            String message,
            String description,
            HttpStatus status,
            Boolean showCallSack
    ) {
        if (showCallSack){
            log.error("{}: {}", ex.getClass().getSimpleName(), ex.getMessage(), ex);
        } else {
            log.error("{}: {}", ex.getClass().getSimpleName(), ex.getMessage());
        }

        ErrorDetails errorDetails = new ErrorDetails(
                message,
                description,
                LocalDateTime.now()
        );
        return new ResponseEntity<>(errorDetails, status);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorDetails> handleResourceNotFound(ResourceNotFoundException ex, WebRequest request) {
        return buildErrorResponse(ex, ex.getMessage(), request.getDescription(false), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorDetails> handleIllegalArgument(IllegalArgumentException ex, WebRequest request) {
        return buildErrorResponse(ex, ex.getMessage(), request.getDescription(false), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ParentHasChildException.class)
    public ResponseEntity<ErrorDetails> handleParentHasChild(ParentHasChildException ex, WebRequest request) {
        return buildErrorResponse(ex, ex.getMessage(), request.getDescription(false), HttpStatus.CONFLICT);
    }

    // 400 - Handle an incorrect input type or input not part of enum
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorDetails> handleTypeMismatch(MethodArgumentTypeMismatchException ex,
                                                           WebRequest request) {
        String paramName = ex.getName();
        String invalidValue = (ex.getValue() != null) ? ex.getValue().toString() : "null";
        Class<?> requiredType = ex.getRequiredType();

        String message;

        if (requiredType != null) {
            if (requiredType.isEnum()) {
                // 🔹 Special handling for enums
                String allowedValues = Arrays.stream(requiredType.getEnumConstants())
                        .map(Object::toString)
                        .collect(Collectors.joining(", "));

                message = String.format(
                        "Invalid value '%s' for parameter '%s'. Allowed values: [%s]",
                        invalidValue, paramName, allowedValues
                );
            } else {
                // 🔹 Handling for other types (int, LocalDate, etc.)
                message = String.format(
                        "Invalid value '%s' for parameter '%s'. Expected type: %s",
                        invalidValue, paramName, requiredType.getSimpleName()
                );
            }
        } else {
            return handleAllExceptions(ex, request);
        }

        ErrorDetails errorDetails = new ErrorDetails(
                message,
                request.getDescription(false),
                LocalDateTime.now()
        );

        return buildErrorResponse(ex, message, request.getDescription(false), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorDetails> handleHttpMessageNotReadable(
            HttpMessageNotReadableException ex,
            WebRequest request
    ) {
        Throwable cause = ex.getCause();

        if (cause instanceof com.fasterxml.jackson.databind.exc.InvalidFormatException ife
        && ife.getTargetType().isEnum()) {
             String message = String.format(
                        "Invalid value '%s' for enum %s. Accepted values: %s",
                        ife.getValue(),
                        ife.getTargetType().getSimpleName(),
                        Arrays.toString(ife.getTargetType().getEnumConstants())
                );

            return buildErrorResponse(ex, message, request.getDescription(false), HttpStatus.BAD_REQUEST);
        } else {
            return handleAllExceptions(ex, request);
        }
    }


    // 405 - Method not allowed
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorDetails> handleMethodNotSupported(HttpRequestMethodNotSupportedException ex, WebRequest request) {
        return buildErrorResponse(ex, "HTTP method not supported", request.getDescription(false),
                HttpStatus.METHOD_NOT_ALLOWED);
    }

    // 404 - Resource Not Found
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ErrorDetails> handleNoResourceFound(NoResourceFoundException ex, WebRequest request) {
        return buildErrorResponse(ex, ex.getMessage(), request.getDescription(false),
                HttpStatus.NOT_FOUND);
    }


    // 400 - Validation errors
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorDetails> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .collect(Collectors.toMap(
                        FieldError::getField,
                        FieldError::getDefaultMessage,
                        (msg1, msg2) -> msg1
                ));

        ErrorDetails errorDetails = new ErrorDetails(
                "Validation failed",
                errors,
                LocalDateTime.now()
        );

        log.error(ex.getClass().getSimpleName() + ": " + ex.getMessage());
        return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
    }

    // 500 - Fallback for all other exceptions
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorDetails> handleAllExceptions(Exception ex, WebRequest request) {
        String safeMessage = "An unexpected error occurred. Please contact support if the problem persists.";
        return buildErrorResponse(ex, safeMessage, request.getDescription(false), HttpStatus.INTERNAL_SERVER_ERROR,
                true);
    }
}
