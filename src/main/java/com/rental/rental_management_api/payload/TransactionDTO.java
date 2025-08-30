package com.rental.rental_management_api.payload;

import com.rental.rental_management_api.model.TransactionType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Data Transfer Object for a Payment")
public class TransactionDTO {
    @Schema(description = "Unique identifier of a payment", example = "1")
    private Integer transactionId;

    @NotNull(message = "Room ID cannot be blank")
    @Schema(description = "Unique identifier of the room", example = "1")
    private Integer roomId;

    @NotNull(message = "Tenant ID cannot be blank")
    @Schema(description = "Unique identifier of the tenant", example = "1")
    private Integer tenantId;

    @NotNull(message = "Transaction Type cannot be blank")
    @Schema(description = "Type of transaction", example = "Rent")
    private TransactionType transactionType;

    @NotNull(message = "Amount cannot be blank")
    @Min(value = 1, message = "Ammount must be atleast 1")
    @Schema(description = "Amount of transaction", example = "3000")
    private Integer amount;

    @NotNull(message = "Payment Month cannot be null")
    @Schema(description = "Represents the month this payment applies to. Date must be the first day of the month",
            example = "2025-08-01")
    private LocalDate forMonthOf;

    @NotNull(message = "Transaction Date cannot be null")
    @Schema(description = "Date of transaction",
            example = "2025-08-23")
    private LocalDate transactionDate;

    @Schema(description = "Notes of transaction",
            example = "Using down-payment for damage")
    private String notes;
}