package com.viddefe.viddefe_api.worship_meetings.application;

import com.viddefe.viddefe_api.notifications.Infrastructure.dto.NotificationDto;
import com.viddefe.viddefe_api.notifications.Infrastructure.factory.NotificatorFactory;
import com.viddefe.viddefe_api.notifications.config.Channels;
import com.viddefe.viddefe_api.notifications.contracts.Notificator;
import com.viddefe.viddefe_api.people.contracts.PeopleReader;
import com.viddefe.viddefe_api.people.domain.model.PeopleModel;
import com.viddefe.viddefe_api.people.infrastructure.dto.PeopleResDto;
import com.viddefe.viddefe_api.worship_meetings.configuration.AttendanceEventType;
import com.viddefe.viddefe_api.worship_meetings.contracts.EventMeetingReaderCaseUse;
import com.viddefe.viddefe_api.worship_meetings.contracts.MinistryFunctionService;
import com.viddefe.viddefe_api.worship_meetings.contracts.MinistryFunctionTypeReader;
import com.viddefe.viddefe_api.worship_meetings.domain.models.MinistryFunction;
import com.viddefe.viddefe_api.worship_meetings.domain.models.MinistryFunctionTypes;
import com.viddefe.viddefe_api.worship_meetings.domain.repository.MinistryFunctionRepository;
import com.viddefe.viddefe_api.worship_meetings.infrastructure.dto.CreateMinistryFunctionDto;
import com.viddefe.viddefe_api.worship_meetings.infrastructure.dto.MeetingDto;
import com.viddefe.viddefe_api.worship_meetings.infrastructure.dto.MinistryFunctionDto;
import com.viddefe.viddefe_api.worship_meetings.infrastructure.dto.MinistryFunctionTypeDto;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MinistryFunctionServiceImpl implements MinistryFunctionService {
    private final MinistryFunctionRepository ministryFunctionRepository;
    private final MinistryFunctionTypeReader ministryFunctionTypeReader;
    private final PeopleReader peopleReader;
    private final EventMeetingReaderCaseUse eventMeetingReaderCaseUse;
    private final NotificatorFactory notificatorFactory;
    private static final String TEMPLATE_ASSIGNED = """
    Hola {{name}} 👋
    
    Has sido asignado a una función ministerial.
    
    📌 Evento: {{eventName}}
    🙌 Rol: {{role}}
    🗓 Fecha: {{date}}
    
    Gracias por servir 💙
    """;

    private static final String TEMPLATE_UPDATED = """
    Hola {{name}} 👋

    Tu asignación ministerial ha sido actualizada.

    📌 Evento: {{eventName}}
    🙌 Rol: {{role}}
    🗓 Fecha: {{date}}

    Por favor revisa los cambios 🙏
    """;


    @Override
    public MinistryFunctionDto create(
            CreateMinistryFunctionDto dto,
            UUID eventId,
            AttendanceEventType eventType
    ) {
        MinistryFunctionTypes role = ministryFunctionTypeReader.findById(dto.getRoleId());
        PeopleModel people = peopleReader.getPeopleById(dto.getPeopleId());

        MinistryFunction entity = new MinistryFunction();
        entity.setEventId(eventId);
        entity.setPeople(people);
        entity.setMinistryFunctionType(role);
        entity.setEventType(eventType);

        MinistryFunction saved = ministryFunctionRepository.save(entity);

        sendNotification(
                people.toDto(),
                eventId,
                role,
                TEMPLATE_ASSIGNED
        );

        return saved.toDto();
    }

    @Override
    public MinistryFunctionDto update(
            UUID id,
            CreateMinistryFunctionDto dto,
            AttendanceEventType eventType
    ) {
        MinistryFunction entity = ministryFunctionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Ministry function not found"));

        MinistryFunctionTypes role = ministryFunctionTypeReader.findById(dto.getRoleId());
        PeopleModel people = peopleReader.getPeopleById(dto.getPeopleId());

        entity.setPeople(people);
        entity.setMinistryFunctionType(role);
        entity.setEventType(eventType);

        MinistryFunction saved = ministryFunctionRepository.save(entity);

        sendNotification(
                people.toDto(),
                entity.getEventId(),
                role,
                TEMPLATE_UPDATED
        );

        return saved.toDto();
    }

    @Override
    public List<MinistryFunctionDto> findByEventId(UUID eventId, AttendanceEventType eventType) {
        return ministryFunctionRepository.findByEventId(eventId).stream().map(MinistryFunction::toDto).toList();
    }

    @Override
    public void delete(UUID id) {
        if(ministryFunctionRepository.findById(id).isEmpty()) {
            throw new EntityNotFoundException("Funcion ministerial no encontrada");
        }
        ministryFunctionRepository.deleteById(id);
    }

    @Override
    public List<MinistryFunctionTypeDto> getAllMinistryFunctionTypes() {
        return ministryFunctionTypeReader.findAll().stream().map(MinistryFunctionTypes::toDto).toList();
    }

    private void sendNotification(PeopleResDto person, UUID eventId, MinistryFunctionTypes role, String template) {
        MeetingDto meetingDto = eventMeetingReaderCaseUse.getMeetingDto(eventId);
        Notificator notificator = notificatorFactory.get(Channels.WHATSAPP);
        NotificationDto notificationDto = new NotificationDto();
        notificationDto.setTo(person.phone());
        notificationDto.setCreatedAt(Instant.now());
        notificationDto.setTemplate(template);
        notificationDto.setVariables(
                java.util.Map.of(
                        "name", person.firstName() + " " + person.lastName(),
                        "date", meetingDto.getScheduledDate().toLocalDate().toString(),
                        "eventName", meetingDto.getName(),
                        "role", role.getName()
                )
        );
        notificator.send(notificationDto);
    }
}
