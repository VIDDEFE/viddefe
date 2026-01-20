package com.viddefe.viddefe_api.worship_meetings.application;

import com.viddefe.viddefe_api.worship_meetings.configuration.TopologyEventType;
import com.viddefe.viddefe_api.worship_meetings.contracts.TopologyMeetingReader;
import com.viddefe.viddefe_api.worship_meetings.domain.models.TopologyMeetingModel;
import com.viddefe.viddefe_api.worship_meetings.domain.repository.TopologyMeetingRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TopologyMeetingReaderImpl implements TopologyMeetingReader {

    private final TopologyMeetingRepository topologyMeetingRepository;

    @Override
    public TopologyMeetingModel findByTopologyMeetingEnum(TopologyEventType type) {
        return topologyMeetingRepository.findByType(type).orElseThrow(
                () -> new EntityNotFoundException("No se encontró una reunión topológica para el tipo: " + type)
        );
    }
}
