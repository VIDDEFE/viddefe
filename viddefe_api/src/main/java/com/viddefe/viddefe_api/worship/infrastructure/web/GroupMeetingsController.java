package com.viddefe.viddefe_api.worship.infrastructure.web;

import com.viddefe.viddefe_api.worship.contracts.GroupMeetingService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/meetings/groups")
@RequiredArgsConstructor
public class GroupMeetingsController {
    private final GroupMeetingService groupMeetingService;
}
