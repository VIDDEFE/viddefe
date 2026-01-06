package com.viddefe.viddefe_api.worship_meetings.domain.seeder;

import com.viddefe.viddefe_api.worship_meetings.configuration.GroupMeetingTypesEnum;
import com.viddefe.viddefe_api.worship_meetings.domain.models.GroupMeetingTypes;
import com.viddefe.viddefe_api.worship_meetings.domain.repository.GroupMeetingTypeRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
public class SeederGroupMeetingTypes {
    private final GroupMeetingTypeRepository groupMeetingTypeRepository;

    @PostConstruct
    public void seed() {
        List<String> existingTypes = groupMeetingTypeRepository.findAll()
                .stream()
                .map(com.viddefe.viddefe_api.worship_meetings.domain.models.GroupMeetingTypes::getName)
                .toList();
        List<GroupMeetingTypes> typesToSeed = Arrays.stream(GroupMeetingTypesEnum.values())
                .filter(groupMeetingTypesEnum -> !existingTypes.contains(groupMeetingTypesEnum.name()))
                .map(gt -> new GroupMeetingTypes(null, gt.getLabel()))
                .toList();
        groupMeetingTypeRepository.saveAll(typesToSeed);
    }
}
