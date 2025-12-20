package com.viddefe.viddefe_api.churches.infrastructure.dto;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;
import java.util.UUID;

@Getter
@Setter
public class ChurchDTO {

    /** id es opcional, por eso no lleva ninguna validación obligatoria */
    private UUID id;

    @NotBlank(message = "El nombre de la iglesia es obligatorio")
    @Size(max = 100, message = "El nombre no puede superar los 100 caracteres")
    private String name;

    @NotNull(message = "No se especifico la ubicacion de la iglesia")
    @Digits(integer = 9, fraction = 20,
            message = "Ubicacion no valida, longitud invalida")
    private BigDecimal longitude;

    @NotNull(message = "No se especifico la ubicacion de la iglesia")
    @Digits(integer = 9, fraction = 20,
            message = "Ubicacion no valida, latitud invalida")
    private BigDecimal latitude;

    @NotNull(message = "No se indico la ciudad perteneciente")
    private Long cityId;

    @NotBlank(message = "El correo electronico es obligatorio")
    @Email(message = "El correo electronico no es valido")
    @Size(max = 100, message = "El correo no puede superar los 100 caracteres")
    private String email;

    @NotNull(message = "El telefono es obligatorio")
    private Long phone;
    @NotNull(message = "La fecha de fundacion es obligatoria")
    private Date foundationDate;

    private UUID pastorId;
}
