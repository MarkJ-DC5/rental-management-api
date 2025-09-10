package com.rental.rental_management_api.mapper;

import com.rental.rental_management_api.entity.Transaction;
import com.rental.rental_management_api.payload.TransactionDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring") // Makes it injectable in Spring
public interface TransactionMapper {

    @Mapping(target = "roomId", expression = "java(transaction.getRoom().getRoomId())")
    @Mapping(target = "tenantId", expression = "java(transaction.getTenant().getTenantId())")
    TransactionDTO toDto(Transaction transaction);

    @Mapping(target = "transactionId", ignore = true)
    @Mapping(target = "room", ignore = true)
    @Mapping(target = "tenant", ignore = true)
    Transaction toEntity(TransactionDTO transactionDto);

    List<TransactionDTO> toDtoList(List<Transaction> transactions);

    List<Transaction> toEntityList(List<TransactionDTO> transactionDtos);
}