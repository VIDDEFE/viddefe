package com.viddefe.viddefe_api.auth.Infrastructure.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter @Setter
public class InvitationDto {
    @NotBlank(message = "Email is required")
    @Email
    private String email;

    @NotNull(message = "Person is required")
    private UUID personId;

    @NotNull(message = "Role is required")
    private Long role;
    @NotNull(message = "Permissions are required")
    private List<String> permissions;
}
