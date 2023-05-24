package com.pokotilov.finaltask.dto;


import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class VoteDto {
  @Min(value = 1, message = "Your vote should be minimum 1")
  @Max(value = 5, message = "Your vote should be maximum 5")
  @NotNull(message = "This field can't be blank.")
  private Long vote;
  @NotNull(message = "Choose an advert.")
  private Long advertId;
}
