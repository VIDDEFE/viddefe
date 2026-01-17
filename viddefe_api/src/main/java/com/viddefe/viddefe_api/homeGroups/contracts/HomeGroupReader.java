package com.viddefe.viddefe_api.homeGroups.contracts;

import com.viddefe.viddefe_api.homeGroups.domain.model.HomeGroupsModel;

import java.util.UUID;

public interface HomeGroupReader {
    HomeGroupsModel findById(UUID groupId);
}
