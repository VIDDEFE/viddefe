package com.viddefe.viddefe_api.worship_meetings.infrastructure.web;

import com.viddefe.viddefe_api.common.Components.JwtUtil;
import com.viddefe.viddefe_api.common.response.ApiResponse;
import com.viddefe.viddefe_api.worship_meetings.configuration.AttendanceQualityEnum;
import com.viddefe.viddefe_api.worship_meetings.configuration.TopologyEventType;
import com.viddefe.viddefe_api.worship_meetings.contracts.MeetingFacade;
import com.viddefe.viddefe_api.worship_meetings.infrastructure.dto.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * Controlador REST centralizado para todas las reuniones (cultos y reuniones de grupo).
 *
 * <p>Usa el parámetro obligatorio {@code type} para determinar el tipo de reunión:</p>
 * <ul>
 *   <li>{@code TEMPLE_WORHSIP} - Cultos/servicios de adoración (contextId = churchId del JWT)</li>
 *   <li>{@code GROUP_MEETING} - Reuniones de grupo (contextId = groupId del request param)</li>
 * </ul>
 *
 * <h3>Ejemplos de uso:</h3>
 * <pre>
 * POST   /meetings?type=TEMPLE_WORHSIP                    → Crear culto (churchId del JWT)
 * POST   /meetings?type=GROUP_MEETING&contextId={uuid}    → Crear reunión de grupo
 * GET    /meetings?type=TEMPLE_WORHSIP                    → Listar cultos de la iglesia
 * GET    /meetings?type=GROUP_MEETING&contextId={uuid}    → Listar reuniones del grupo
 * GET    /meetings/{id}?type=TEMPLE_WORHSIP               → Obtener culto por ID
 * PUT    /meetings/{id}?type=TEMPLE_WORHSIP               → Actualizar culto
 * DELETE /meetings/{id}?type=TEMPLE_WORHSIP               → Eliminar culto
 * </pre>
 */
@RestController
@RequestMapping("/meetings")
@RequiredArgsConstructor
public class MeetingsController {

    private final MeetingFacade meetingFacade;
    private final JwtUtil jwtUtil;

    // ==================== CREATE ====================

    /**
     * Crea una nueva reunión.
     *
     * @param type Tipo de reunión (obligatorio): TEMPLE_WORHSIP o GROUP_MEETING
     * @param contextId ID del contexto (obligatorio para GROUP_MEETING, ignorado para TEMPLE_WORHSIP)
     * @param dto Datos de la reunión a crear
     * @param accessToken Token JWT con información del usuario
     * @return La reunión creada
     */
    @PostMapping
    public ResponseEntity<ApiResponse<MeetingDto>> createMeeting(
            @RequestParam TopologyEventType type,
            @RequestParam(required = false) UUID contextId,
            @RequestBody @Validated(OnCreate.class) CreateMeetingDto dto,
            @CookieValue("access_token") String accessToken
    ) {
        UUID churchId = jwtUtil.getChurchId(accessToken);
        UUID resolvedContextId = resolveContextId(type, contextId, accessToken);
        MeetingDto response = meetingFacade.createMeeting(dto, resolvedContextId, type, churchId);
        return new ResponseEntity<>(ApiResponse.created(response), HttpStatus.CREATED);
    }

    // ==================== READ ====================

    /**
     * Lista todas las reuniones del contexto con paginación.
     *
     * @param type Tipo de reunión (obligatorio)
     * @param contextId ID del contexto (obligatorio para GROUP_MEETING)
     * @param pageable Información de paginación
     * @param accessToken Token JWT
     * @return Página de reuniones
     */
    @GetMapping
    public ResponseEntity<ApiResponse<Page<MeetingDto>>> getAllMeetings(
            @RequestParam TopologyEventType type,
            @RequestParam(required = false) UUID contextId,
            Pageable pageable,
            @CookieValue("access_token") String accessToken
    ) {
        UUID resolvedContextId = resolveContextId(type, contextId, accessToken);
        Page<MeetingDto> response = meetingFacade.getAllMeetings(resolvedContextId, type, pageable);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    /**
     * Obtiene una reunión por su ID.
     *
     * @param id ID de la reunión
     * @param type Tipo de reunión (obligatorio)
     * @param contextId ID del contexto (obligatorio para GROUP_MEETING)
     * @param accessToken Token JWT
     * @return La reunión encontrada
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<MeetingDto>> getMeetingById(
            @PathVariable UUID id,
            @RequestParam TopologyEventType type,
            @RequestParam(required = false) UUID contextId,
            @CookieValue("access_token") String accessToken
    ) {
        UUID resolvedContextId = resolveContextId(type, contextId, accessToken);
        MeetingDto response = meetingFacade.getMeetingById(resolvedContextId, id, type);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    // ==================== UPDATE ====================

    /**
     * Actualiza una reunión existente.
     *
     * @param id ID de la reunión a actualizar
     * @param type Tipo de reunión (obligatorio)
     * @param contextId ID del contexto (obligatorio para GROUP_MEETING)
     * @param dto Datos actualizados de la reunión
     * @param accessToken Token JWT
     * @return La reunión actualizada
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<MeetingDto>> updateMeeting(
            @PathVariable UUID id,
            @RequestParam TopologyEventType type,
            @RequestParam(required = false) UUID contextId,
            @RequestBody @Validated(OnUpdate.class) CreateMeetingDto dto,
            @CookieValue("access_token") String accessToken
    ) {
        UUID resolvedContextId = resolveContextId(type, contextId, accessToken);
        MeetingDto response = meetingFacade.updateMeeting(dto, resolvedContextId, id, type);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    // ==================== DELETE ====================

    /**
     * Elimina una reunión.
     *
     * @param id ID de la reunión a eliminar
     * @param type Tipo de reunión (obligatorio)
     * @param contextId ID del contexto (obligatorio para GROUP_MEETING)
     * @param accessToken Token JWT
     * @return 204 No Content
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteMeeting(
            @PathVariable UUID id,
            @RequestParam TopologyEventType type,
            @RequestParam(required = false) UUID contextId,
            @CookieValue("access_token") String accessToken
    ) {
        UUID resolvedContextId = resolveContextId(type, contextId, accessToken);
        meetingFacade.deleteMeeting(resolvedContextId, id, type);
        return new ResponseEntity<>(ApiResponse.noContent(), HttpStatus.NO_CONTENT);
    }

    // ==================== ATTENDANCE ====================

    /**
     * Registra o actualiza la asistencia a una reunión.
     *
     * @param type Tipo de reunión (obligatorio)
     * @param dto Datos de asistencia
     * @return Asistencia registrada
     */
    @PutMapping("/attendance")
    public ResponseEntity<ApiResponse<AttendanceDto>> recordAttendance(
            @RequestParam TopologyEventType type,
            @RequestBody @Valid CreateAttendanceDto dto
    ) {
        AttendanceDto response = meetingFacade.recordAttendance(dto, type);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    /**
     * Obtiene la lista de asistencia de una reunión.
     *
     * @param id ID de la reunión
     * @param type Tipo de reunión (obligatorio)
     * @param pageable Información de paginación
     * @return Página de asistencias
     */
    @GetMapping("/{id}/attendance")
    public ResponseEntity<ApiResponse<Page<AttendanceDto>>> getAttendance(
            @PathVariable UUID id,
            @RequestParam TopologyEventType type,
            @RequestParam(required = false) UUID groupId,
            Pageable pageable,
            @CookieValue("access_token") String accessToken,
            @RequestParam(required = false) AttendanceQualityEnum levelOfAttendance
    ) {
        UUID contextId = resolveContextId(type, groupId, accessToken);
        Page<AttendanceDto> response = meetingFacade.getAttendance(id, type, pageable, contextId, levelOfAttendance);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    /**
     * Resuelve el contextId según el tipo de evento.
     * Para TEMPLE_WORHSIP: usa churchId del JWT.
     * Para GROUP_MEETING: usa el contextId del request param (obligatorio).
     */
    private UUID resolveContextId(TopologyEventType type, UUID contextId, String accessToken) {
        return switch (type) {
            case TEMPLE_WORHSIP -> jwtUtil.getChurchId(accessToken);
            case GROUP_MEETING -> {
                if (contextId == null) {
                    throw new IllegalArgumentException(
                            "El parámetro 'groupId' es obligatorio para reuniones de tipo: Reuniones de Grupo"
                    );
                }
                yield contextId;
            }
        };
    }
}

