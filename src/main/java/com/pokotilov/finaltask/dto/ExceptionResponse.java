package com.pokotilov.finaltask.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.Map;

@Getter
public class ExceptionResponse {
    private final HttpStatus status;
    private String message;
    private Map<String, String> map;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime time = LocalDateTime.now();

    public ExceptionResponse(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }

    public ExceptionResponse(HttpStatus status, Map<String, String> map) {
        this.status = status;
        this.map = map;
    }

    public ExceptionResponse(HttpStatus status, String message, Map<String, String> map) {
        this.status = status;
        this.message = message;
        this.map = map;
    }
}
