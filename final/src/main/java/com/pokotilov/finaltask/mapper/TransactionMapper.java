package com.pokotilov.finaltask.mapper;

import com.pokotilov.finaltask.dto.TransactionDto;
import com.pokotilov.finaltask.entities.TransactionRecord;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TransactionMapper {
    TransactionDto toDto(TransactionRecord transactionRecord);
}
