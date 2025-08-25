package com.rental.rental_management_api.service;

import com.rental.rental_management_api.entity.Transaction;
import com.rental.rental_management_api.entity.Room;
import com.rental.rental_management_api.entity.Tenant;
import com.rental.rental_management_api.exception.ResourceNotFoundException;
import com.rental.rental_management_api.mapper.PageMapper;
import com.rental.rental_management_api.mapper.TransactionMapper;
import com.rental.rental_management_api.mapper.RoomMapper;
import com.rental.rental_management_api.mapper.TenantMapper;
import com.rental.rental_management_api.payload.TransactionDTO;
import com.rental.rental_management_api.repository.TransactionRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Transactional
@AllArgsConstructor
@Slf4j
public class TransactionService {
    private final TransactionRepository transactionRepository;

    private final RoomMapper roomMapper;
    private final TenantMapper tenantMapper;
    private final TransactionMapper transactionMapper;
    private final PageMapper pageMapper;

    private final RoomService roomService;
    private final TenantService tenantService;

    private Transaction getTransactionOrThrow(Integer transactionId) {
        return transactionRepository.findById(transactionId)
                .orElseThrow(
                        () -> new ResourceNotFoundException("Transaction", transactionId)
                );
    }

    public TransactionDTO getTransactionById(Integer transactionId) {
        return transactionMapper.toDto(getTransactionOrThrow(transactionId));
    }

    public TransactionDTO saveTransaction(TransactionDTO transactionDto) {
        Room room = roomService.getRoomOrThrow(transactionDto.getRoomId());
        Tenant primaryTenant = room.getPrimaryTenant();

        if (!primaryTenant.getTenantId().equals(transactionDto.getTenantId())) {
            throw new IllegalArgumentException("Provided Primary Tenant ID " + transactionDto.getTenantId()
                    + " does match with the Primary Tenant ID in Room " + transactionDto.getRoomId());
        }

        Transaction transaction = transactionMapper.toEntity(transactionDto);
        transaction.setRoom(room);
        transaction.setTenant(primaryTenant);

        return transactionMapper.toDto(transactionRepository.save(transaction));
    }

    public TransactionDTO updateTransaction(Integer transactionId, TransactionDTO transactionDtoUpdate) {
        Transaction transaction = getTransactionOrThrow(transactionId);

        Room room = roomService.getRoomOrThrow(transactionDtoUpdate.getRoomId());
        Tenant primaryTenant = room.getPrimaryTenant();

        if (!primaryTenant.getTenantId().equals(transactionDtoUpdate.getTenantId())) {
            throw new IllegalArgumentException("Provided Primary Tenant ID " + transactionDtoUpdate.getTenantId()
                    + " does match with the Primary Tenant ID in Room " + transactionDtoUpdate.getRoomId());
        }

        transaction.setRoom(room);
        transaction.setTenant(primaryTenant);
        transaction.setTransactionType(transactionDtoUpdate.getTransactionType());
        transaction.setAmount(transactionDtoUpdate.getAmount());
        transaction.setForMonthOf(transactionDtoUpdate.getForMonthOf());
        transaction.setTransactionDate(transactionDtoUpdate.getTransactionDate());
        transaction.setNotes(transactionDtoUpdate.getNotes());

        return transactionMapper.toDto(transaction);
    }


    /*



    public void deleteTransaction(Integer transactionId) {
        Transaction payment = getTransactionOrThrow(transactionId);

        paymentRepository.delete(payment);
    }

     */
}