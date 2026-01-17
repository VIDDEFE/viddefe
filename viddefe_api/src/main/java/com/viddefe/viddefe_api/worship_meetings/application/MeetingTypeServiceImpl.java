package com.viddefe.viddefe_api.worship_meetings.application;

import com.viddefe.viddefe_api.worship_meetings.configuration.TopologyEventType;
import com.viddefe.viddefe_api.worship_meetings.contracts.MeetingTypesService;
import com.viddefe.viddefe_api.worship_meetings.domain.models.MeetingType;
import com.viddefe.viddefe_api.worship_meetings.domain.models.TopologyMeetingModel;
import com.viddefe.viddefe_api.worship_meetings.domain.repository.MeetingTypeRepository;
import com.viddefe.viddefe_api.worship_meetings.domain.repository.TopologyMeetingRepository;
import com.viddefe.viddefe_api.worship_meetings.infrastructure.dto.MeetingTypeDto;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MeetingTypeServiceImpl implements MeetingTypesService {
    private final MeetingTypeRepository meetingTypeConfigRepository;
    private final TopologyMeetingRepository topologyMeetingRepository;

    @Override
    public List<MeetingTypeDto> getAllMeetingByTopologyEventTypes(TopologyEventType topologyEventType) {
        TopologyMeetingModel topologyMeetingModel = topologyMeetingRepository.findByType(topologyEventType)
                .orElseThrow(() -> new EntityNotFoundException("Topology Meeting with type " + topologyEventType + " not found"));
        return topologyMeetingModel.getMeetingTypes()
                .stream().map(MeetingType::toDto).toList();
    }

    @Override
    public MeetingType getMeetingTypesById(Long id) {
        return meetingTypeConfigRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Meeting Type with id " + id + " not found")
        );
    }
}
