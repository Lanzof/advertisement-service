package com.pokotilov.finaltask.dto.comments;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
  private LocalDateTime date;
  private String text;
  private Boolean ban;

}
