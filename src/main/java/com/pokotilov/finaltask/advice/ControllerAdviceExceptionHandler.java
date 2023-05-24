package com.pokotilov.finaltask.advice;

import com.pokotilov.finaltask.dto.ExceptionResponse;
import com.pokotilov.finaltask.exceptions.SelfVoteException;
import com.pokotilov.finaltask.exceptions.UserAlreadyExistException;
import com.pokotilov.finaltask.exceptions.UserNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class ControllerAdviceExceptionHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public final ExceptionResponse handleUserNotFoundExceptions(UserNotFoundException ex) {
//        log.error(ex.getMessage(), ex);
        return new ExceptionResponse(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    public final ExceptionResponse handleValidationExceptions(MethodArgumentNotValidException ex) {
//        log.error(ex.getMessage(), ex);
        Map<String, String> violations = new HashMap<>();
        for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            violations.put(fieldError.getField(), fieldError.getDefaultMessage());
        }
        return new ExceptionResponse(HttpStatus.UNPROCESSABLE_ENTITY, "Data validation error", violations);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    public final ExceptionResponse handleSelfVoteExceptions(SelfVoteException ex) {
//        log.error(ex.getMessage(), ex);
        return new ExceptionResponse(HttpStatus.UNPROCESSABLE_ENTITY, ex.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public final ExceptionResponse handleUserAlreadyExistExceptions(UserAlreadyExistException ex) {
//        log.error(ex.getMessage(), ex);
        return new ExceptionResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public final ExceptionResponse handleAccessDeniedExceptions(AccessDeniedException ex) {
//        log.error(ex.getMessage(), ex);
        return new ExceptionResponse(HttpStatus.UNAUTHORIZED, "Unauthorized");
    }
}
