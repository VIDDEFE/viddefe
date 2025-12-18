package com.viddefe.viddefe_api.churches.domain.seeder;

import com.viddefe.viddefe_api.auth.Infrastructure.dto.PermissionSeedRequest;
import com.viddefe.viddefe_api.auth.contracts.PermissionService;
import com.viddefe.viddefe_api.churches.configuration.ChurchPermissions;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ChurchPermissionsSeeder {
    private final PermissionService permissionService;

    @PostConstruct
    public void seed() {
        permissionService.seed(
                new PermissionSeedRequest(
                        "CHURCHES",
                        List.of(ChurchPermissions.values())
                )
        );
    }
}
