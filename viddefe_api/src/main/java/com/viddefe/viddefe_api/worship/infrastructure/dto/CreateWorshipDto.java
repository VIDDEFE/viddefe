package com.viddefe.viddefe_api.worship.infrastructure.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter @Setter
public class CreateWorshipDto {
    @NotBlank(message = "Name is required")
    @Size(max = 120, message = "Name must not exceed 120 characters")
    private String name;

    @Size(max = 500, message = "Description must not exceed 500 characters")
    private String description;

    @NotNull(message = "Scheduled date is required")
    @FutureOrPresent(message = "Scheduled date cannot be in the past")
    private LocalDateTime scheduledDate;

    @NotNull(message = "Worship type ID is required")
    private Long worshipTypeId;
}
