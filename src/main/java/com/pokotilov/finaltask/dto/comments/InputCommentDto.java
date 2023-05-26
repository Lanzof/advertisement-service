package com.pokotilov.finaltask.dto.comments;


import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class InputCommentDto {
  @Positive
  @NotNull
  @JsonProperty("advert ID")
  private Long advertId;
  @NotBlank
  @Size(min = 4, max = 10000, message = "Data validation error")
  private String text;

}
