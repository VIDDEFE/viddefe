package com.viddefe.viddefe_api.auth.service;

import com.viddefe.viddefe_api.auth.model.RolUserModel;
import com.viddefe.viddefe_api.auth.repository.RolUserRepository;
import com.viddefe.viddefe_api.common.exception.CustomExceptions;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RolesUserService {
    private final RolUserRepository rolUserRepository;

    public RolUserModel foundRolUserById(Long id){
        return rolUserRepository.findById(id).orElseThrow(
            () -> new CustomExceptions.ResourceNotFoundException("Rol User not found")
        );
    }
}
