package com.viddefe.viddefe_api.worship.application;

import com.viddefe.viddefe_api.worship.contracts.GroupMeetingService;
import com.viddefe.viddefe_api.worship.domain.repository.GroupMeetingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GroupMeetingServiceImpl implements GroupMeetingService {
    private final GroupMeetingRepository groupMeetingRepository;
}
