package com.atlas.bank.atlas_bank.infrastructure.adapter.in.rest.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;

@Data
@DifferentAccounts
public class TransferRequest {

    @NotNull(message="La cuenta de origen es obligatoria")
    private Long sourceAccountId;

    @NotNull(message = "La cuenta destino es ogligatoria")
    private Long targetAccountId;

    @NotNull(message= "El monto es obligatorio")
    @Positive (message ="La cantidad de la transferencia debe ser positivo")
    private BigDecimal amount;
}
