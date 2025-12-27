package com.viddefe.viddefe_api.homeGroups.infrastructure.dto;

import jakarta.validation.constraints.*;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
public class CreateHomeGroupsDto {

    @NotBlank(message = "El nombre es obligatorio")
    @Size(min = 3, max = 100, message = "El nombre debe tener entre 3 y 100 caracteres")
    private String name;

    @Size(max = 255, message = "La descripción no puede superar los 255 caracteres")
    private String description;

    @NotNull(message = "La latitud es obligatoria")
    @DecimalMin(value = "-90.0", inclusive = true, message = "La latitud mínima es -90")
    @DecimalMax(value = "90.0", inclusive = true, message = "La latitud máxima es 90")
    @Digits(integer = 3, fraction = 15, message = "La latitud debe tener máximo 2 enteros y 8 decimales")
    private BigDecimal latitude;

    @NotNull(message = "La longitud es obligatoria")
    @DecimalMin(value = "-180.0", inclusive = true, message = "La longitud mínima es -180")
    @DecimalMax(value = "180.0", inclusive = true, message = "La longitud máxima es 180")
    @Digits(integer = 3, fraction = 15, message = "La longitud debe tener máximo 3 enteros y 8 decimales")
    private BigDecimal longitude;

    @NotNull(message = "La estrategia es obligatorio")
    private UUID strategyId;

    @NotNull(message = "El líder es obligatorio")
    private UUID leaderId;
}