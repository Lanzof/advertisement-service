package com.pokotilov.finaltask.exceptions;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.ZonedDateTime;

@Data
@AllArgsConstructor
public class ExceptionResponse {
    private ZonedDateTime timeStamp;
    private String message;
    private String detail;

}
