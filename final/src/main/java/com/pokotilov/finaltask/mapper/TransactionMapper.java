package com.pokotilov.finaltask.mapper;

import com.pokotilov.finaltask.dto.TransactionDto;
import com.pokotilov.finaltask.entities.Transaction;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TransactionMapper {
    TransactionDto toDto(Transaction transaction);
}
