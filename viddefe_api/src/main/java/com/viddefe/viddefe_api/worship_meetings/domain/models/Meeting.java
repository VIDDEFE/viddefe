package com.viddefe.viddefe_api.worship_meetings.domain.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Clase base abstracta para todas las entidades de reuniones/meetings.
 * Usa @MappedSuperclass para compartir campos comunes sin crear una tabla padre.
 *
 * Reglas de timezone:
 * - scheduledDate: OffsetDateTime (timestamptz en BD) - fecha programada con zona
 * - creationDate: Instant (timestamptz en BD) - momento de creación en UTC
 * - NO usar ZoneId.systemDefault() ni conversiones de zona en mappers/services
 */
@MappedSuperclass
@Getter @Setter
public abstract class Meeting {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String name;

    private String description;

    /**
     * Fecha de creación - Instant representa un punto en el tiempo en UTC.
     * Se asigna automáticamente al crear la entidad: Instant.now()
     */
    @Column(name = "creation_date", nullable = false, updatable = false, columnDefinition = "timestamptz")
    private Instant creationDate;

    /**
     * Fecha programada del meeting - OffsetDateTime preserva el offset del cliente.
     * El frontend envía ISO-8601 con offset (ej: "2026-01-15T18:00:00-05:00" o "2026-01-15T23:00:00Z").
     * Se almacena en BD como timestamptz (Postgres convierte a UTC internamente).
     * Se devuelve al cliente con el offset UTC o el original según configuración de Jackson.
     */
    @Column(name = "scheduled_date", nullable = false, columnDefinition = "timestamptz")
    private OffsetDateTime scheduledDate;

    /**
     * Inicializa los campos comunes desde un DTO base.
     * No realiza conversiones de zona - asigna directamente.
     *
     * @param name nombre del meeting
     * @param description descripción opcional
     * @param scheduledDate fecha programada (con offset del cliente)
     */
    protected void initFromDto(String name, String description, OffsetDateTime scheduledDate) {
        this.name = name;
        this.description = description;
        this.scheduledDate = scheduledDate;
        this.creationDate = Instant.now();
    }

    /**
     * Actualiza los campos comunes desde un DTO (no modifica creationDate).
     *
     * @param name nombre del meeting
     * @param description descripción opcional
     * @param scheduledDate fecha programada (con offset del cliente)
     */
    protected void updateFromDto(String name, String description, OffsetDateTime scheduledDate) {
        this.name = name;
        this.description = description;
        this.scheduledDate = scheduledDate;
    }
}

