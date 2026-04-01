package com.viddefe.viddefe_api.notifications.Infrastructure.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO for marking notifications as read request.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MarkAsReadRequestDto {

    /**
     * Whether to mark all notifications as read (true)
     * or just a specific notification (false)
     */
    private boolean markAllAsRead;
}
