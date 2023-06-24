package com.pokotilov.finaltask.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WalletDto {
    private Double balance;
    private Long userId;
    private String firstName;
    private String lastName;
}
