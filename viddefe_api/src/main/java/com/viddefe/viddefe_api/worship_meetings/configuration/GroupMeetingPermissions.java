package com.viddefe.viddefe_api.worship_meetings.configuration;

import com.viddefe.viddefe_api.auth.contracts.PermissionEnum;

public enum GroupMeetingPermissions implements PermissionEnum {
    GROUP_MEETING_ADD,
    GROUP_MEETING_VIEW,
    GROUP_MEETING_EDIT,
    GROUP_MEETING_DELETE;

    @Override
    public String getName() {
        return this.name();
    }
}
