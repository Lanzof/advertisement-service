package com.pokotilov.finaltask.advice;

import com.pokotilov.finaltask.exceptions.ExceptionResponse;
import com.pokotilov.finaltask.exceptions.UserNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ControllerAdviceExceptionHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public final ExceptionResponse handleUserNotFoundExceptions(UserNotFoundException ex) {
//        log.error(ex.getMessage(), ex);
        return new ExceptionResponse(HttpStatus.NOT_FOUND, ex.getMessage());
    }


}
