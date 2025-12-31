package com.viddefe.viddefe_api.homeGroups.domain.seeder;

import com.viddefe.viddefe_api.auth.Infrastructure.dto.PermissionSeedRequest;
import com.viddefe.viddefe_api.auth.contracts.PermissionService;
import com.viddefe.viddefe_api.homeGroups.configuration.GroupPermissions;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class SeederGroupPermissions {
    private final PermissionService permissionService;

    @PostConstruct
    public void init() {
        permissionService.seed(
                new PermissionSeedRequest(
                        "GROUP_SEEDER",
                        List.of(GroupPermissions.values())
                )
        );
    }
}
