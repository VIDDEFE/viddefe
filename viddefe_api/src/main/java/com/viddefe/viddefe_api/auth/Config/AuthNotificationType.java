package com.viddefe.viddefe_api.auth.Config;

import com.viddefe.viddefe_api.notifications.contracts.NotificationType;

public enum AuthNotificationType implements NotificationType {

    INVITATION,
    PASSWORD_RESET,
    ACCOUNT_VERIFICATION;


    @Override
    public String getValue() {
        return this.name();
    }
}
