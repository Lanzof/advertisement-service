package com.pokotilov.finaltask.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.ZonedDateTime;

@RestControllerAdvice
public class CustomizeResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

//    private ResponseEntity<?> handle(Exception ex, WebRequest request, HttpStatus httpStatus) {
//        String message = ex.getMessage();
//        if (message == null) {
//            message = "Message not provided";
//        }
//
//        ServletWebRequest servletWebRequest =  (ServletWebRequest) request;
//        String uri = servletWebRequest.getRequest().getRequestURI();
//
//        RestErrorDto restErrorDto = new RestErrorDto(message, ex.getClass().getSimpleName(), httpStatus.value(), uri);
//        ExceptionResponse exceptionResponse= new ExceptionResponse(new Date(), ex.getMessage(), request.getDescription(false));
//        return handleExceptionInternal(ex, message, new HttpHeaders(), httpStatus, request);
////        return new ResponseEntity(exceptionResponse, HttpStatus.NOT_FOUND);
//
//    }
//
    @ExceptionHandler(value = {UserNotFoundException.class})
    public final ResponseEntity<?> handleUserNotFoundExceptions(UserNotFoundException ex) {
        HttpStatus status = HttpStatus.NOT_FOUND;
        ExceptionResponse exceptionResponse = new ExceptionResponse(ZonedDateTime.now(), ex.getMessage(), " ");
        return new ResponseEntity<>(exceptionResponse, status);
    }
//    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
//        return handleExceptionInternal(ex, null, headers, status, request);
//    }



}
