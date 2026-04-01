package com.viddefe.viddefe_api.auth.infrastructure.dto;

import com.viddefe.viddefe_api.auth.contracts.PermissionEnum;

import java.util.List;

public record PermissionSeedRequest(
        String source,
        List<? extends PermissionEnum> permissions
) {}
