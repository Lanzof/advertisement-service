package com.pokotilov.finaltask.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CommentDto {
  @Positive
  private long advertId;
  private long authorId;
  private LocalDateTime date;
  @NotBlank
  @Size(min = 4, max = 10000, message = "Data validation error")
  private String text;

}
