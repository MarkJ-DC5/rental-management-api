package com.rental.rental_management_api.controller;


import com.rental.rental_management_api.payload.PageResponse;
import com.rental.rental_management_api.payload.TransactionDTO;
import com.rental.rental_management_api.service.TransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.SortDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.time.LocalDate;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Tag(name = "4. Transaction")
public class TransactionController {

    private final TransactionService service;

    @GetMapping(path = "/transactions")
    @Operation(summary = "Retrieve all transactions between a specified Date Range")
    public ResponseEntity<PageResponse<TransactionDTO>> getTransactions(
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate,
            @ParameterObject @SortDefault.SortDefaults({
                    @SortDefault(sort = "transactionDate", direction = Sort.Direction.DESC),
                    @SortDefault(sort = "datetimeUpdated", direction = Sort.Direction.DESC)
            }) Pageable pageable
    ) {
        return ResponseEntity.ok(service.getAllTransactions(startDate, endDate, pageable));
    }

    @GetMapping(path = "/transaction/{transactionId}")
    @Operation(summary = "Retrieve a transactions by its ID")
    public ResponseEntity<TransactionDTO> getTransactionId(
            @PathVariable Integer transactionId
    ) {
        return ResponseEntity.ok(service.getTransactionById(transactionId));
    }

    @PostMapping(path = "/transactions")
    @Operation(summary = "Create a New Transaction")
    public ResponseEntity<TransactionDTO> postTransaction(
            @RequestBody @Valid TransactionDTO transactionDto
    ) {
        transactionDto = service.saveTransaction(transactionDto);

        URI location =
                ServletUriComponentsBuilder.fromCurrentRequest().path("/{tenantId}")
                        .buildAndExpand(transactionDto.getTenantId()).toUri();

        return ResponseEntity.created(location).body(transactionDto);
    }

    @PutMapping(path = "/transactions/{transactionId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update a transactions")
    public ResponseEntity<TransactionDTO> putTransaction(
            @PathVariable Integer transactionId,
            @RequestBody @Valid TransactionDTO transactionDto) {
        return ResponseEntity.ok(service.updateTransaction(transactionId, transactionDto));
    }

    @DeleteMapping(path = "/transactions/{transactionId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete a transaction by its ID")
    public ResponseEntity<Void> deleteRoom(
            @PathVariable Integer transactionId) {
        service.deleteTransaction(transactionId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping(path = "/rooms/{roomId}/transactions")
    @Operation(summary = "Retrieve all transactions for the specified room between a specified Date Range")
    public ResponseEntity<PageResponse<TransactionDTO>> getTransactionsByRoomId(
            @PathVariable Integer roomId,
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate,
            @ParameterObject @SortDefault(sort = "transactionDate", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return ResponseEntity.ok(service.getTransactionsByRoomId(roomId, startDate, endDate, pageable));
    }

    @GetMapping(path = "/tenants/{tenantId}/transactions")
    @Operation(summary = "Retrieve all transactions for the specified tenant between a specified Date Range")
    public ResponseEntity<PageResponse<TransactionDTO>> getTransactionsByTenantId(
            @PathVariable Integer tenantId,
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate,
            @ParameterObject @SortDefault(sort = "transactionDate", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return ResponseEntity.ok(service.getTransactionsByTenantId(tenantId, startDate, endDate, pageable));
    }
}