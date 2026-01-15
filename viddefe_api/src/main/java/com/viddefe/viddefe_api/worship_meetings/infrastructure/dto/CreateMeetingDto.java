package com.viddefe.viddefe_api.worship_meetings.infrastructure.dto;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;

/**
 * DTO base para creación de meetings/reuniones.
 *
 * <p>Usa deserialización polimórfica de Jackson basada en el campo "meetingType":</p>
 * <ul>
 *   <li>{@code "WORSHIP"} → {@link CreateWorshipDto}</li>
 *   <li>{@code "GROUP_MEETING"} → {@link CreateMeetingGroupDto}</li>
 * </ul>
 *
 * <h3>Reglas de timezone (obligatorias):</h3>
 * <ul>
 *   <li>scheduledDate DEBE incluir offset (ej: "2026-01-15T18:00:00-05:00" o "2026-01-15T23:00:00Z")</li>
 *   <li>Si el JSON no incluye offset, Jackson rechazará la petición con 400 Bad Request</li>
 *   <li>El backend NO asume ninguna zona por defecto</li>
 *   <li>El frontend es responsable de convertir hora local a ISO-8601 con offset</li>
 * </ul>
 *
 * <h3>Ejemplo de JSON para crear culto:</h3>
 * <pre>
 * {
 *   "meetingType": "WORSHIP",
 *   "name": "Culto dominical",
 *   "description": "Servicio de adoración",
 *   "scheduledDate": "2026-01-15T10:00:00-05:00",
 *   "worshipTypeId": 1
 * }
 * </pre>
 *
 * <h3>Ejemplo de JSON para crear reunión de grupo:</h3>
 * <pre>
 * {
 *   "meetingType": "GROUP_MEETING",
 *   "name": "Estudio bíblico",
 *   "description": "Reunión semanal",
 *   "scheduledDate": "2026-01-15T19:00:00-05:00",
 *   "groupMeetingTypeId": 1
 * }
 * </pre>
 */
@Getter @Setter
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "meetingType",
        visible = true
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = CreateWorshipDto.class, name = "WORSHIP"),
        @JsonSubTypes.Type(value = CreateMeetingGroupDto.class, name = "GROUP_MEETING")
})
public abstract class CreateMeetingDto {

    @NotBlank(message = "El nombre de la reunión es obligatorio")
    @Size(max = 120, message = "El nombre no debe exceder 120 caracteres")
    private String name;

    @Size(max = 500, message = "La descripción no debe exceder 500 caracteres")
    private String description;

    /**
     * Fecha programada del meeting.
     * DEBE incluir zona horaria (offset).
     * Formato: ISO-8601 con offset (ej: "2026-01-15T18:00:00-05:00" o "2026-01-15T23:00:00Z")
     */
    @NotNull(message = "La fecha programada es obligatoria")
    @FutureOrPresent(message = "La fecha programada no puede ser en el pasado")
    private OffsetDateTime scheduledDate;
}
