package com.viddefe.viddefe_api.auth.dto;

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
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&._-])[A-Za-z\\d@$!%*?&._-]+$",
            message = "The password must contain at least one uppercase letter, one lowercase letter, one number, and one special character"
    )
    private String password;
    private Long roleId;
}
