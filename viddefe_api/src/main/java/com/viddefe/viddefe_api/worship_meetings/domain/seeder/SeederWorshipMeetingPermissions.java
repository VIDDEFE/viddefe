package com.viddefe.viddefe_api.worship_meetings.domain.seeder;

import com.viddefe.viddefe_api.auth.Infrastructure.dto.PermissionSeedRequest;
import com.viddefe.viddefe_api.auth.contracts.PermissionService;
import com.viddefe.viddefe_api.worship_meetings.configuration.WorshipMeetingPermissions;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class SeederWorshipMeetingPermissions {
    private final PermissionService permissionService;

    @PostConstruct
    public void init(){
        permissionService.seed(
                new PermissionSeedRequest(
                        "WORSHIP_MEETINGS",
                        List.of(WorshipMeetingPermissions.values())
                )
        );
    }

}
