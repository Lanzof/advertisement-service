package com.pokotilov.finaltask.dto.advert;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OutputAdvertDto {
    private Long id;
    private String title;
    private String description;
    @DateTimeFormat(pattern = "dd-MM-yyyy HH:mm:ss")
    private LocalDateTime date;
    private Integer price;
    private Boolean premium;
    private Long userId;
    private String firstName;
    private String lastName;
    private Integer commentsCount;
}
