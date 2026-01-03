package com.viddefe.viddefe_api.finances.infrastructure.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.util.UUID;

@Getter
public class CreateOfferingDto {
    private UUID eventId;
    @NotNull(message = "No se especificó el monto de la ofrenda")
    @NotNull(message = "No se especificó el monto de la ofrenda")
    @DecimalMin(value = "0.0", inclusive = false, message = "El monto debe ser mayor a cero")
    private Double amount;

    @NotNull(message = "No se especificó la persona que realiza la ofrenda")
    private UUID peopleId;

    @NotNull(message = "No se especificó el tipo de ofrenda")
    private Long offeringTypeId;

}
