package com.viddefe.viddefe_api.churches;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
public class ChurchDTO {

    /** id es opcional, por eso no lleva ninguna validación obligatoria */
    private UUID id;

    @NotBlank(message = "El nombre de la iglesia es obligatorio")
    @Size(max = 100, message = "El nombre no puede superar los 100 caracteres")
    private String name;

    @NotNull(message = "La longitud (longitude) es obligatoria")
    @Digits(integer = 9, fraction = 6,
            message = "La longitud debe tener hasta 9 dígitos enteros y 6 decimales")
    private BigDecimal longitude;

    @NotNull(message = "El stateId es obligatorio")
    @Positive(message = "El stateId debe ser un número positivo")
    private BigDecimal stateId;
}
