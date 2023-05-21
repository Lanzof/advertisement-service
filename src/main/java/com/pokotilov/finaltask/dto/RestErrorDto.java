package com.pokotilov.finaltask.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RestErrorDto {

    private String message;

    private String type;

    private Integer status;

    private String instance;

}