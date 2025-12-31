package com.viddefe.viddefe_api.auth.Config;

import com.viddefe.viddefe_api.auth.contracts.PermissionEnum;

public enum ManageUsersPermission implements PermissionEnum {
    INVITATION_PERMISSION,
    REMOVE_USER_INVITATION,
    VIEW_USER_INVITATION,
    DELETE_USER;

    @Override
    public String getName() {
        return this.name();
    }
}
