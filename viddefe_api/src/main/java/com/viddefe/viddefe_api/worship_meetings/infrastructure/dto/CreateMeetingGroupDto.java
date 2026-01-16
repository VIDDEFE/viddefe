package com.viddefe.viddefe_api.worship_meetings.infrastructure.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;

/**
 * DTO para creación de reuniones de grupo/hogar.
 * Extiende de CreateMeetingDto para heredar campos comunes (name, description, scheduledDate).
 *
 * Nota: Este DTO mantiene compatibilidad con el campo 'date' existente en la API
 * mediante un getter/setter que mapea a scheduledDate del padre.
 *
 * Ejemplo de JSON válido:
 * {
 *   "name": "Reunión semanal",
 *   "description": "Estudio bíblico",
 *   "date": "2026-01-15T19:00:00-05:00",
 *   "groupMeetingTypeId": 1
 * }
 */
@Getter @Setter
public class CreateMeetingGroupDto extends CreateMeetingDto {

    @NotNull(message = "El tipo de reunión de grupo es obligatorio")
    private Long groupMeetingTypeId;

    /**
     * Getter de compatibilidad para 'date'.
     * Mapea al campo scheduledDate heredado de CreateMeetingDto.
     *
     * @return la fecha programada
     */
    public OffsetDateTime getDate() {
        return getScheduledDate();
    }

    /**
     * Setter de compatibilidad para 'date'.
     * Mapea al campo scheduledDate heredado de CreateMeetingDto.
     *
     * @param date la fecha programada
     */
    public void setDate(OffsetDateTime date) {
        setScheduledDate(date);
    }
}
