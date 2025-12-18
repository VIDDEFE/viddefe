package com.viddefe.viddefe_api.people.domain.seeder;

import com.viddefe.viddefe_api.auth.Infrastructure.dto.PermissionSeedRequest;
import com.viddefe.viddefe_api.auth.contracts.PermissionService;
import com.viddefe.viddefe_api.people.config.PeoplePermissions;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class PeoplePermissionSeeder {

    private final PermissionService permissionService;

    @PostConstruct
    public void seed() {
        permissionService.seed(
                new PermissionSeedRequest(
                        "PEOPLE",
                        List.of(PeoplePermissions.values())
                )
        );
    }
}
