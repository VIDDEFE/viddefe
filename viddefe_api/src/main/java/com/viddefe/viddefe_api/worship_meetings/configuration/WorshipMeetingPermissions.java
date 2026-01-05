package com.viddefe.viddefe_api.worship_meetings.configuration;

import com.viddefe.viddefe_api.auth.contracts.PermissionEnum;

public enum WorshipMeetingPermissions implements PermissionEnum {
    WORSHIP_ADD_MEETING,
    WORSHIP_VIEW_MEETING,
    WORSHIP_EDIT_MEETING,
    WORSHIP_DELETE_MEETING;

    @Override
    public String getName() {
        return this.name();
    }
}
