package com.viddefe.viddefe_api.worship_meetings.infrastructure.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

/**
 * DTO para creación de cultos/servicios de adoración.
 * Extiende de CreateMeetingDto para heredar campos comunes (name, description, scheduledDate).
 * 
 * Ejemplo de JSON válido:
 * {
 *   "name": "Culto dominical",
 *   "description": "Servicio de adoración matutino",
 *   "scheduledDate": "2026-01-15T10:00:00-05:00",
 *   "worshipTypeId": 1
 * }
 */
@Getter @Setter
public class CreateWorshipDto extends CreateMeetingDto {
    
    @NotNull(message = "El tipo de culto es obligatorio")
    private Long worshipTypeId;
}
