package com.pokotilov.finaltask.dto;


import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class VoteDto {
  @NotBlank(message = "Choose a user.")
  private long recipientId;
  @Min(value = 1, message = "Your vote should be minimum 1")
  @Max(value = 5, message = "Your vote should be maximum 5")
  @NotBlank(message = "This field can't be blank.")
  private long vote;
  @NotBlank
  private long advert_id;
}
