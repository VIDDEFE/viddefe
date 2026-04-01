package com.viddefe.viddefe_api.auth.domain.seeder;

import java.util.List;

import org.springframework.stereotype.Component;

import com.viddefe.viddefe_api.auth.config.ManageUsersPermission;
import com.viddefe.viddefe_api.auth.contracts.PermissionService;
import com.viddefe.viddefe_api.auth.infrastructure.dto.PermissionSeedRequest;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ManageUserPermission {

    private final PermissionService permissionService;

    @PostConstruct
    public void init() {
        permissionService.seed(
                new PermissionSeedRequest(
                        "MANAGE_USERS",
                        List.of(ManageUsersPermission.values())
                )
        );
    }
}
