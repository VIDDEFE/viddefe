package com.viddefe.viddefe_api.notifications.Infrastructure.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * DTO for paginated notification list response.
 * Wraps the page of notifications with metadata.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationListResponseDto {

    /**
     * List of notifications for the current page
     */
    private List<UserNotificationResponseDto> notifications;

    /**
     * Total number of notifications
     */
    private long totalElements;

    /**
     * Total number of pages
     */
    private int totalPages;

    /**
     * Current page number (0-indexed)
     */
    private int currentPage;

    /**
     * Size of the current page
     */
    private int pageSize;

    /**
     * Whether there are more pages after this one
     */
    private boolean hasNextPage;

    /**
     * Whether this is the first page
     */
    private boolean isFirstPage;

    /**
     * Whether this is the last page
     */
    private boolean isLastPage;

    /**
     * Create a NotificationListResponseDto from a Spring Page object
     * 
     * @param page The Spring Page object with UserNotificationResponseDto items
     * @return The DTO with pagination metadata
     */
    public static NotificationListResponseDto from(Page<UserNotificationResponseDto> page) {
        return NotificationListResponseDto.builder()
                .notifications(page.getContent())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .currentPage(page.getNumber())
                .pageSize(page.getSize())
                .hasNextPage(page.hasNext())
                .isFirstPage(page.isFirst())
                .isLastPage(page.isLast())
                .build();
    }
}
