package com.pokotilov.finaltask.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.List;

@Data
public class DefaultResponse {
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<Object> list;
    @JsonInclude(JsonInclude.Include.NON_NULL)
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