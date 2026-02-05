package com.viddefe.viddefe_api.worship_meetings.domain.seeder;

import com.viddefe.viddefe_api.worship_meetings.configuration.TempleMeetingTypes;
import com.viddefe.viddefe_api.worship_meetings.configuration.TopologyEventType;
import com.viddefe.viddefe_api.worship_meetings.contracts.TopologyMeetingReader;
import com.viddefe.viddefe_api.worship_meetings.domain.models.MeetingType;
import com.viddefe.viddefe_api.worship_meetings.domain.models.TopologyMeetingModel;
import com.viddefe.viddefe_api.worship_meetings.domain.repository.MeetingTypeRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
public class SeederWorshipMeetingTypes {
    private final MeetingTypeRepository meetingTypeRepository;
    private final TopologyMeetingReader topologyMeetingReader;

    @PostConstruct
    public void seed() {
        TopologyMeetingModel topologyGroupModel = topologyMeetingReader.findByTopologyMeetingEnum(
                TopologyEventType.TEMPLE_WORHSIP
        );
        List<String> existingTypes = meetingTypeRepository.findAll()
                .stream()
                .map(MeetingType::getName)
                .toList();
        List<MeetingType> typesToSeed = Arrays.stream(TempleMeetingTypes.values())
                .filter(templeMeetingTypes -> !existingTypes.contains(templeMeetingTypes.getLabel()))
                .map(gt -> new MeetingType(null, topologyGroupModel,gt.getLabel()))
                .toList();
        meetingTypeRepository.saveAll(typesToSeed);
    }
}
