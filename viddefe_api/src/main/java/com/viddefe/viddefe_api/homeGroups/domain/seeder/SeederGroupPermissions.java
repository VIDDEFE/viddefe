package com.viddefe.viddefe_api.homegroups.domain.seeder;

import java.util.List;

import org.springframework.stereotype.Component;

import com.viddefe.viddefe_api.auth.contracts.PermissionService;
import com.viddefe.viddefe_api.auth.infrastructure.dto.PermissionSeedRequest;
import com.viddefe.viddefe_api.homegroups.configuration.GroupPermissions;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;

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
