package com.pokotilov.finaltask.advice;

import com.pokotilov.finaltask.dto.ExceptionResponse;
import com.pokotilov.finaltask.exceptions.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestControllerAdvice
@Slf4j
public class ControllerAdviceExceptionHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<ExceptionResponse> handleNotFoundExceptions(NotFoundException ex, HttpServletRequest request) {
        makeLog(ex, request);
        ExceptionResponse body = new ExceptionResponse(HttpStatus.NOT_FOUND, ex.getMessage());
        return ResponseEntity.status(body.getStatus()).body(body);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.EXPECTATION_FAILED)
    public ResponseEntity<ExceptionResponse> handleExpectationFailedExceptions(ExpectationFailedException ex, HttpServletRequest request) {
        makeLog(ex, request);
        ExceptionResponse body = new ExceptionResponse(HttpStatus.EXPECTATION_FAILED, ex.getMessage());
        return ResponseEntity.status(body.getStatus()).body(body);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    public ResponseEntity<ExceptionResponse> handleUnprocessableEntityExceptions(UnprocessableEntityException ex, HttpServletRequest request) {
        makeLog(ex, request);
        ExceptionResponse body = new ExceptionResponse(HttpStatus.UNPROCESSABLE_ENTITY, ex.getMessage());
        return ResponseEntity.status(body.getStatus()).body(body);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ResponseEntity<ExceptionResponse> handleAccessDeniedExceptions(ConflictException ex, HttpServletRequest request) {
        makeLog(ex, request);
        ExceptionResponse body = new ExceptionResponse(HttpStatus.CONFLICT, ex.getMessage());
        return ResponseEntity.status(body.getStatus()).body(body);
    }


    @ExceptionHandler
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    protected ResponseEntity<ExceptionResponse> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex, HttpServletRequest request) {
        makeLog(ex, request);
        Map<String, String> violations = new HashMap<>();
        for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            violations.put(fieldError.getField(), fieldError.getDefaultMessage());
        }
        ExceptionResponse body = new ExceptionResponse(HttpStatus.UNPROCESSABLE_ENTITY, "Data validation error", violations);
        return ResponseEntity.status(body.getStatus()).body(body);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    public ResponseEntity<ExceptionResponse> handleValidationExceptions(ConstraintViolationException ex, HttpServletRequest request) {
        makeLog(ex, request);
        Map<String, String> violations = new HashMap<>();
        for (ConstraintViolation<?> fieldError : ex.getConstraintViolations()) {
            violations.put(String.valueOf(fieldError.getPropertyPath()), fieldError.getMessage());
        }
        ExceptionResponse body = new ExceptionResponse(HttpStatus.UNPROCESSABLE_ENTITY, "Data validation error", violations);
        return ResponseEntity.status(body.getStatus()).body(body);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    public ResponseEntity<ExceptionResponse> handleMethodArgumentTypeMismatch(MethodArgumentTypeMismatchException ex, HttpServletRequest request) {
        makeLog(ex, request);
        ExceptionResponse body = new ExceptionResponse(HttpStatus.UNPROCESSABLE_ENTITY, ex.getMessage());
        return ResponseEntity.status(body.getStatus()).body(body);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ExceptionResponse> handleBadRequestExceptions(BadRequestException ex, HttpServletRequest request) {
        makeLog(ex, request);
        ExceptionResponse body = new ExceptionResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
        return ResponseEntity.status(body.getStatus()).body(body);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ResponseEntity<ExceptionResponse> handleAccessDeniedExceptions(AccessDeniedException ex, HttpServletRequest request) {
        makeLog(ex, request);
        ExceptionResponse body = new ExceptionResponse(HttpStatus.UNAUTHORIZED, "Access Denied");
        return ResponseEntity.status(body.getStatus()).body(body);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ResponseEntity<ExceptionResponse> handleAuthenticationExceptions(AuthenticationException ex, HttpServletRequest request) {
        makeLog(ex, request);
        ExceptionResponse body = new ExceptionResponse(HttpStatus.UNAUTHORIZED, ex.getMessage());
        return ResponseEntity.status(body.getStatus()).body(body);
    }

    private static void makeLog(Exception ex, HttpServletRequest request) {
        String principal = Optional.ofNullable(request.getUserPrincipal()).isPresent() ? ", user principal: " + request.getUserPrincipal() : "";
        log.error(request.getMethod() + " " + request.getRequestURI() + " error message: " + ex.getMessage() + principal, ex);
    }
}
