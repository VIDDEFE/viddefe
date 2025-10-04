package com.viddefe.viddefe_api.people.dto;

import lombok.Getter;
import lombok.Setter;

import jakarta.validation.constraints.*;
import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
public class PeopleDTO {

    @NotBlank(message = "La cédula (cc) es obligatoria")
    @Size(max = 20, message = "La cédula no debe superar los 20 caracteres")
    private String cc;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 50, message = "El nombre no debe superar los 50 caracteres")
    private String firstName;

    @NotBlank(message = "El apellido es obligatorio")
    @Size(max = 50, message = "El apellido no debe superar los 50 caracteres")
    private String lastName;

    @NotBlank(message = "El teléfono es obligatorio")
    @Pattern(
            regexp = "^[0-9+\\-\\s]{7,20}$",
            message = "El teléfono debe tener entre 7 y 20 dígitos y solo puede incluir números, +, - o espacios"
    )
    private String phone;

    @Size(max = 255, message = "La URL del avatar no debe superar los 255 caracteres")
    @Pattern(
            regexp = "^(https?://.*)?$",
            message = "El avatar debe ser una URL válida (http o https) o estar vacío"
    )
    private String avatar;

    @NotNull(message = "La fecha de nacimiento es obligatoria")
    @Past(message = "La fecha de nacimiento debe ser en el pasado")
    private LocalDate birthDate;

    @NotNull(message = "El tipo de persona es obligatorio")
    private Long typePersonId;

    @NotNull(message = "El estado es obligatorio")
    private Long stateId;

    private UUID churchId;
}
