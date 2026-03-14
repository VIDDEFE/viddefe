package com.viddefe.viddefe_api.auth.application;

import org.springframework.stereotype.Service;

import com.viddefe.viddefe_api.auth.domain.model.RolUserModel;
import com.viddefe.viddefe_api.auth.domain.repository.RolUserRepository;
import com.viddefe.viddefe_api.common.exception.CustomExceptions;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RolesUserService {
    private final RolUserRepository rolUserRepository;

    public RolUserModel foundRolUserById(@NonNull Long id){
        return rolUserRepository.findById(id).orElseThrow(
            () -> new CustomExceptions.ResourceNotFoundException("Rol User not found")
        );
    }
}
