package com.viddefe.viddefe_api.people.config;

import com.viddefe.viddefe_api.auth.contracts.PermissionEnum;

public enum PeoplePermissions implements PermissionEnum {
    PEOPLE_ADD_PEOPLE,
    PEOPLE_VIEW_PEOPLE,
    PEOPLE_EDIT_PEOPLE,
    PEOPLE_DELETE_PEOPLE;

    @Override
    public String getName() {
        return this.name();
    }
}
