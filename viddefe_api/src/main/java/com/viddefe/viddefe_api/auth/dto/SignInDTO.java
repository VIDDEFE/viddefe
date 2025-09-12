package com.viddefe.viddefe_api.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SignInDTO {
    @NotBlank(message = "Email is empty")
    @Email(message = "Is not valid format email")
    private String email;
    @NotBlank(message = "Password is empty")
    private String password;
}
