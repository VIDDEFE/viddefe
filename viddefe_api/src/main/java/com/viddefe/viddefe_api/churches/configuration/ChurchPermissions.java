package com.viddefe.viddefe_api.churches.configuration;

import com.viddefe.viddefe_api.auth.contracts.PermissionEnum;

public enum ChurchPermissions implements PermissionEnum {
    CHURCH_ADD_CHILDREN,
    CHURCH_VIEW_CHILDREN,
    CHURCH_EDIT_CHILDREN,
    CHURCH_DELETE_CHILDREN;

    @Override
    public String getName() {
        return this.name();
    }
}
