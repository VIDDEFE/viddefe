package com.viddefe.viddefe_api.homeGroups.domain.model;

import com.viddefe.viddefe_api.homeGroups.contracts.HomeGroupReader;
import com.viddefe.viddefe_api.homeGroups.domain.repository.HomeGroupsRepository;
import com.viddefe.viddefe_api.worship_meetings.domain.models.GroupMeetings;
import com.viddefe.viddefe_api.worship_meetings.domain.models.MeetingTypeEnum;
import com.viddefe.viddefe_api.worship_meetings.infrastructure.dto.GroupMeetingDto;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class HomeGroupsReaderImpl implements HomeGroupReader {
    private final HomeGroupsRepository homeGroupsRepository;
    @Override
    public HomeGroupsModel findById(UUID groupId) {
        return homeGroupsRepository.findById(groupId).orElseThrow(
                () -> new EntityNotFoundException("No se encontró el grupo")
        );
    }
}
