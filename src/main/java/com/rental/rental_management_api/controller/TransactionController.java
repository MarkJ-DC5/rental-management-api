package com.rental.rental_management_api.controller;


import com.rental.rental_management_api.payload.TransactionDTO;
import com.rental.rental_management_api.service.TransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@AllArgsConstructor
@Tag(name = "4. Transaction", description = "Endpoints for transaction management")
public class TransactionController {

    private final TransactionService service;

    @GetMapping(path = "/transaction/{transactionId}")
    @Operation(summary = "Retrieve a payment by its ID.")
    public ResponseEntity<TransactionDTO> getTransactionId(
            @PathVariable Integer transactionId
    ) {
        return ResponseEntity.ok(service.getTransactionById(transactionId));
    }

    @PostMapping(path = "/payments")
    @Operation(summary = "Create a New Transaction.")
    public ResponseEntity<TransactionDTO> postTransaction(
            @RequestBody @Valid TransactionDTO transactionDto
    ) {
        transactionDto = service.saveTransaction(transactionDto);

        URI location =
                ServletUriComponentsBuilder.fromCurrentRequest().path("/{tenantId}")
                        .buildAndExpand(transactionDto.getTenantId()).toUri();

        return ResponseEntity.created(location).body(transactionDto);
    }

    @PutMapping(path = "/payments/{transactionId}")
    @Operation(summary = "Update a payment.")
    public ResponseEntity<TransactionDTO> putTransaction(
            @PathVariable Integer transactionId,
            @RequestBody @Valid TransactionDTO transactionDto) {
        return ResponseEntity.ok(service.updateTransaction(transactionId, transactionDto));
    }
}