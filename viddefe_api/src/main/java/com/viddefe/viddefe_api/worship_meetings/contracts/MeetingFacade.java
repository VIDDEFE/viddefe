package com.viddefe.viddefe_api.worship_meetings.contracts;

import com.viddefe.viddefe_api.worship_meetings.configuration.TopologyEventType;
import com.viddefe.viddefe_api.worship_meetings.infrastructure.dto.AttendanceDto;
import com.viddefe.viddefe_api.worship_meetings.infrastructure.dto.CreateAttendanceDto;
import com.viddefe.viddefe_api.worship_meetings.infrastructure.dto.CreateMeetingDto;
import com.viddefe.viddefe_api.worship_meetings.infrastructure.dto.MeetingDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

/**
 * Facade para operaciones unificadas de reuniones (cultos y reuniones de grupo).
 * Orquesta los servicios específicos según el tipo de evento {@link TopologyEventType}.
 *
 * <p>El contextId representa:</p>
 * <ul>
 *   <li>Para TEMPLE_WORSHIP: churchId (obtenido del JWT)</li>
 *   <li>Para GROUP_MEETING: groupId (obtenido del path)</li>
 * </ul>
 */
public interface MeetingFacade {

    // ==================== CREATE ====================

    /**
     * Crea una nueva reunión según el tipo de evento.
     *
     * @param dto DTO con datos de la reunión (debe ser del tipo correcto según eventType)
     * @param contextId churchId para cultos, groupId para reuniones de grupo
     * @param eventType tipo de evento que determina qué servicio usar
     * @param churchId ID de la iglesia
     * @return MeetingDto con los datos de la reunión creada
     */
    MeetingDto createMeeting(CreateMeetingDto dto, UUID contextId, TopologyEventType eventType, UUID churchId);

    // ==================== READ ====================

    /**
     * Obtiene una reunión por su ID.
     *
     * @param contextId churchId o groupId según el tipo
     * @param meetingId ID de la reunión
     * @param eventType tipo de evento
     * @return MeetingDto con los datos de la reunión (incluye detalles de asistencia)
     */
    MeetingDto getMeetingById(UUID contextId, UUID meetingId, TopologyEventType eventType);

    /**
     * Lista las reuniones paginadas por contexto.
     *
     * @param contextId churchId o groupId según el tipo
     * @param eventType tipo de evento
     * @param pageable información de paginación
     * @return Page de MeetingDto
     */
    Page<MeetingDto> getAllMeetings(UUID contextId, TopologyEventType eventType, Pageable pageable);

    // ==================== UPDATE ====================

    /**
     * Actualiza una reunión existente.
     *
     * @param dto DTO con datos actualizados
     * @param contextId churchId o groupId según el tipo
     * @param meetingId ID de la reunión a actualizar
     * @param eventType tipo de evento
     * @return MeetingDto con los datos actualizados
     */
    MeetingDto updateMeeting(CreateMeetingDto dto, UUID contextId, UUID meetingId, TopologyEventType eventType);

    // ==================== DELETE ====================

    /**
     * Elimina una reunión.
     *
     * @param contextId churchId o groupId según el tipo
     * @param meetingId ID de la reunión a eliminar
     * @param eventType tipo de evento
     */
    void deleteMeeting(UUID contextId, UUID meetingId, TopologyEventType eventType);

    // ==================== ATTENDANCE ====================

    /**
     * Registra o actualiza la asistencia de una persona a una reunión.
     *
     * @param dto datos de asistencia
     * @param eventType tipo de evento
     * @return AttendanceDto con los datos de asistencia registrados
     */
    AttendanceDto recordAttendance(CreateAttendanceDto dto, TopologyEventType eventType);

    /**
     * Obtiene la lista de asistencia de una reunión.
     *
     * @param meetingId ID de la reunión
     * @param eventType tipo de evento
     * @param pageable información de paginación
     * @return Page de AttendanceDto
     */
    Page<AttendanceDto> getAttendance(UUID meetingId, TopologyEventType eventType, Pageable pageable);
}
