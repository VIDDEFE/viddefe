package com.viddefe.viddefe_api.worship_meetings.contracts;

import com.viddefe.viddefe_api.worship_meetings.domain.models.MinistryFunction;

public interface MinistryFunctionReminderSentWriter {
    void writeMinistryFunctionReminderSent(MinistryFunction ministryFunction);
}
