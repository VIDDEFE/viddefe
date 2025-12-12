package com.viddefe.viddefe_api.auth.Infrastructure.dto;

import com.viddefe.viddefe_api.auth.domain.model.RolUserModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SignInResDTO {
    private String email;
    private RolUserModel rolUserModel;
    private String fullName;
    private java.util.UUID personId;
}
