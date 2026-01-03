package com.viddefe.viddefe_api.auth.Infrastructure.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SignInDTO {
    @Email(message = "Is not valid format email")
    private String email;
    private String phone;
    @NotBlank(message = "Password is empty")
    private String password;
}
