package com.viddefe.viddefe_api.auth.Infrastructure.dto;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class SignUpDTO {
    @NotNull(message = "The people id is null")
    private UUID peopleId;

    @NotBlank
    @Email
    private String email;
    @NotBlank(message = "The password is empty")
    @Size(min = 8, max = 20, message = "The password must be between 8 and 20 characters")
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*\\d).+$",
            message = "The password must contain at least one lowercase letter and one number"
    )
    private String password;
    private Long roleId;
}
