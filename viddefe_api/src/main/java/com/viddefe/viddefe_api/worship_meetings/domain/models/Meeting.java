package com.viddefe.viddefe_api.worship_meetings.domain.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Entidad normalizada para todas las reuniones del sistema.
 * Usa herencia JPA con discriminador para diferenciar tipos:
 * - WORSHIP: Cultos/servicios de adoración
 * - GROUP_MEETING: Reuniones de grupos pequeños
 *
 * Tabla unificada: 'meetings'
 * Columna discriminadora: 'meeting_type' (ENUM)
 *
 * Reglas de timezone:
 * - scheduledDate: OffsetDateTime (timestamptz en BD) - fecha con zona del cliente
 * - creationDate: Instant (UTC) - momento de creación
 * - NO usar ZoneId.systemDefault() ni conversiones de zona
 * - El frontend envía ISO-8601 con offset, backend almacena en UTC
 */
@Entity
@Table(
    name = "meetings",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uk_context_type_scheduled",
            columnNames = {"context_id", "meeting_type", "scheduled_date"}
        )
    }
)
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "meeting_type", discriminatorType = DiscriminatorType.STRING)
@Getter @Setter
@AllArgsConstructor @NoArgsConstructor
public abstract class Meeting {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String name;

    private String description;

    /**
     * Fecha de creación - Instant en UTC
     */
    @Column(name = "creation_date", nullable = false, updatable = false, columnDefinition = "timestamptz")
    private Instant creationDate;

    /**
     * Fecha programada con offset del cliente
     */
    @Column(name = "scheduled_date", nullable = false, columnDefinition = "timestamptz")
    private OffsetDateTime scheduledDate;

    /**
     * contextId puede ser:
     * - church_id (para WORSHIP)
     * - home_group_id (para GROUP_MEETING)
     */
    @Column(name = "context_id", nullable = false)
    private UUID contextId;

    /**
     * typeId referencias a:
     * - WorshipMeetingTypes.id (para WORSHIP)
     * - GroupMeetingTypes.id (para GROUP_MEETING)
     */
    @Column(name = "type_id", nullable = false)
    private Long typeId;

    /**
     * Inicializa los campos comunes desde un DTO base.
     * No realiza conversiones de zona - asigna directamente.
     */
    protected void initFromDto(String name, String description, OffsetDateTime scheduledDate) {
        this.name = name;
        this.description = description;
        this.scheduledDate = scheduledDate;
        this.creationDate = Instant.now();
    }

    /**
     * Actualiza los campos comunes desde un DTO (no modifica creationDate).
     */
    protected void updateFromDto(String name, String description, OffsetDateTime scheduledDate) {
        this.name = name;
        this.description = description;
        this.scheduledDate = scheduledDate;
    }
}

