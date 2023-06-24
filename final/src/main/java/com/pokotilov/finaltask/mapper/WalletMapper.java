package com.pokotilov.finaltask.mapper;

import com.pokotilov.finaltask.dto.WalletDto;
import com.pokotilov.finaltask.entities.Wallet;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface WalletMapper {
    @Mapping(target = "userId",
            expression = "java(wallet.getUser().getId())")
    @Mapping(target = "firstName",
            expression = "java(wallet.getUser().getFirstName())")
    @Mapping(target = "lastName",
            expression = "java(wallet.getUser().getLastName())")
    WalletDto toDto(Wallet wallet);
}
