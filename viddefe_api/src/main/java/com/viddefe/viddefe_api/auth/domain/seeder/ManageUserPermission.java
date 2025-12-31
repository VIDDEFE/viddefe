package com.viddefe.viddefe_api.auth.domain.seeder;

import com.viddefe.viddefe_api.auth.Config.ManageUsersPermission;
import com.viddefe.viddefe_api.auth.Infrastructure.dto.PermissionSeedRequest;
import com.viddefe.viddefe_api.auth.contracts.PermissionService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

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
