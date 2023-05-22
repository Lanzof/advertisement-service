package com.pokotilov.finaltask.dto;

import lombok.Data;

import java.util.List;

@Data
public class DefaultResponse {
    private List<Object> list;
    private String message;

    public DefaultResponse(List<Object> list) {
        this.list = list;
    }

    public DefaultResponse(String message) {
        this.message = message;
    }

    public DefaultResponse(List<Object> list, String message) {
        this.list = list;
        this.message = message;
    }
}