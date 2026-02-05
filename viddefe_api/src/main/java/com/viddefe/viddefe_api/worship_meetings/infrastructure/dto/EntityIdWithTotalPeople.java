package com.viddefe.viddefe_api.worship_meetings.infrastructure.dto;

import java.util.UUID;

public interface EntityIdWithTotalPeople {
    UUID getEntityId();
    Long getTotalPeople();
}
