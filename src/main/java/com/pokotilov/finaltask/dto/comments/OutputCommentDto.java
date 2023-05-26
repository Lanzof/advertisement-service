package com.pokotilov.finaltask.dto.comments;


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
public class OutputCommentDto {
  private Long id;
  private Long advertId;
  private Long authorId;
  private String authorFirstName;
  private String authorLastName;
  @DateTimeFormat(pattern = "dd-MM-yyyy HH:mm:ss")
  private LocalDateTime date;
  private String text;
  private Boolean ban;

}
