package com.viddefe.viddefe_api.homeGroups.configuration;

import com.viddefe.viddefe_api.auth.contracts.PermissionEnum;

public enum GroupPermissions implements PermissionEnum {
    CREATE_GROUP,
    VIEW_GROUP,
    EDIT_GROUP,
    DELETE_GROUP;

    @Override
    public String getName() {
        return this.name();
    }
}
