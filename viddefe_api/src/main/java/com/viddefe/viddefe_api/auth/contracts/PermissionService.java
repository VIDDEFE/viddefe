package com.viddefe.viddefe_api.auth.contracts;

import com.viddefe.viddefe_api.auth.Infrastructure.dto.PermissionSeedRequest;
import com.viddefe.viddefe_api.auth.domain.model.PermissionModel;

import java.util.List;

public interface PermissionService {
    List<PermissionModel> findAll();
    void seed(PermissionSeedRequest request);
}
