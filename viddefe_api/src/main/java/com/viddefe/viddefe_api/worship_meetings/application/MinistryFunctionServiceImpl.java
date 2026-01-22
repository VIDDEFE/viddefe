package com.viddefe.viddefe_api.worship_meetings.application;

import com.viddefe.viddefe_api.notifications.Infrastructure.dto.NotificationMeetingEvent;
import com.viddefe.viddefe_api.notifications.common.Channels;
import com.viddefe.viddefe_api.notifications.common.RabbitPriority;
import com.viddefe.viddefe_api.notifications.contracts.NotificationEventPublisher;
import com.viddefe.viddefe_api.people.contracts.PeopleReader;
import com.viddefe.viddefe_api.people.domain.model.PeopleModel;
import com.viddefe.viddefe_api.people.infrastructure.dto.PeopleResDto;
import com.viddefe.viddefe_api.worship_meetings.configuration.TopologyEventType;
import com.viddefe.viddefe_api.worship_meetings.contracts.MeetingReader;
import com.viddefe.viddefe_api.worship_meetings.contracts.MinistryFunctionService;
import com.viddefe.viddefe_api.worship_meetings.contracts.MinistryFunctionTypeReader;
import com.viddefe.viddefe_api.worship_meetings.domain.models.Meeting;
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
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MinistryFunctionServiceImpl implements MinistryFunctionService {
    private final MinistryFunctionRepository ministryFunctionRepository;
    private final MinistryFunctionTypeReader ministryFunctionTypeReader;
    private final PeopleReader peopleReader;
    private final MeetingService meetingService;
    private final NotificationEventPublisher notificatorPublisher;
    private final MeetingReader meetingReader;
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
            TopologyEventType eventType
    ) {
        MinistryFunctionTypes role = ministryFunctionTypeReader.findById(dto.getRoleId());
        PeopleModel people = peopleReader.getPeopleById(dto.getPeopleId());
        if(people.getPhone()==null || people.getPhone().isBlank()){
            throw new IllegalArgumentException("La persona debe tener un número de teléfono válido para ser " +
                    "asignada a una función ministerial.");
        }
        Meeting event = meetingReader.getById(eventId);
        MinistryFunction entity = new MinistryFunction();
        entity.setMeeting(event);
        entity.setPeople(people);
        entity.setMinistryFunctionType(role);
        entity.setEventType(eventType);

        MinistryFunction saved = ministryFunctionRepository.save(entity);

        sendNotification(
                people.toDto(),
                saved.getMeeting(),
                role,
                TEMPLATE_ASSIGNED
        );

        return saved.toDto();
    }

    @Override
    public MinistryFunctionDto update(
            UUID id,
            CreateMinistryFunctionDto dto,
            TopologyEventType eventType
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
                saved.getMeeting(),
                role,
                TEMPLATE_UPDATED
        );

        return saved.toDto();
    }

    @Override
    public List<MinistryFunctionDto> findByEventId(UUID eventId, TopologyEventType eventType) {
        return ministryFunctionRepository.findByMeetingId(eventId).stream().map(MinistryFunction::toDto).toList();
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

    private void sendNotification(PeopleResDto person, Meeting meeting, MinistryFunctionTypes role, String template) {
        MeetingDto meetingDto = meeting.toDto();
        NotificationMeetingEvent event = new NotificationMeetingEvent();
        System.out.println("MEETING: " + meeting.getId().toString());
        event.setMeetingId(meeting.getId());
        event.setCreatedAt(Instant.now());
        event.setPersonId(person.id());
        event.setTemplate(template);
        event.setPriority(RabbitPriority.MEDIUM);
        event.setVariables(
                java.util.Map.of(
                        "name", person.firstName() + " " + person.lastName(),
                        "date", meetingDto.getScheduledDate().toLocalDate().toString(),
                        "eventName", meetingDto.getName(),
                        "role", role.getName()
                )
        );
        event.setChannels(Channels.WHATSAPP);
        System.out.println("EVENNT ENVENTID: "+event.getMeetingId().toString());
        notificatorPublisher.publish(event);
    }

    private boolean shouldSendReminder(
            MinistryFunction mf,
            OffsetDateTime now,
            int daysBefore,
            int hoursBefore
    ) {
        OffsetDateTime scheduled = mf.getMeeting().getScheduledDate();

        // Window start: X days before
        OffsetDateTime windowStart = scheduled.minusDays(daysBefore);

        // Window end: X hours before
        OffsetDateTime windowEnd = scheduled.minusHours(hoursBefore);

        if (now.isBefore(windowStart)) return false;
        if (now.isAfter(windowEnd)) return false;

        // One reminder per day
        Instant lastSent = mf.getReminderSentAt();
        if (lastSent != null) {
            OffsetDateTime lastSentDay =
                    lastSent.atOffset(ZoneOffset.UTC).toLocalDate().atStartOfDay().atOffset(ZoneOffset.UTC);

            OffsetDateTime today =
                    now.toLocalDate().atStartOfDay().atOffset(now.getOffset());

            if (!lastSentDay.isBefore(today)) {
                return false;
            }
        }

        return true;
    }

}
