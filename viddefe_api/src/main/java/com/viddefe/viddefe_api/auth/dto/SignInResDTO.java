package com.viddefe.viddefe_api.auth.dto;

import com.viddefe.viddefe_api.auth.model.RolUserModel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@AllArgsConstructor
@Getter
@Setter
public class SignInResDTO {
    private String email;
    private RolUserModel rolUserModel;
    private String fullName;
    private UUID personId;
}
