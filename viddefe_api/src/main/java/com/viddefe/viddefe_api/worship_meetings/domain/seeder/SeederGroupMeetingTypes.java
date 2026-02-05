package com.viddefe.viddefe_api.worship_meetings.domain.seeder;

import com.viddefe.viddefe_api.worship_meetings.configuration.GroupMeetingTypesEnum;
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
public class SeederGroupMeetingTypes {
    private final MeetingTypeRepository meetingTypeRepository;
    private final TopologyMeetingReader topologyMeetingReader;

    @PostConstruct
    public void seed() {
        TopologyMeetingModel topologyGroupModel = topologyMeetingReader.findByTopologyMeetingEnum(
                TopologyEventType.GROUP_MEETING
        );
        List<String> existingTypes = meetingTypeRepository.findAll()
                .stream()
                .map(MeetingType::getName)
                .toList();
        List<MeetingType> typesToSeed = Arrays.stream(GroupMeetingTypesEnum.values())
                .filter(groupMeetingTypesEnum -> !existingTypes.contains(groupMeetingTypesEnum.getLabel()))
                .map(gt -> new MeetingType(null, topologyGroupModel,gt.getLabel()))
                .toList();
        meetingTypeRepository.saveAll(typesToSeed);
    }
}
