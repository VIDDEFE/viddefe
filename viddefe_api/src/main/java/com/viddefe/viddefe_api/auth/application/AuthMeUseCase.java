package com.viddefe.viddefe_api.auth.application;

import com.viddefe.viddefe_api.auth.Infrastructure.dto.UserInfo;
import com.viddefe.viddefe_api.auth.contracts.AuthMeService;
import com.viddefe.viddefe_api.auth.domain.model.UserModel;
import com.viddefe.viddefe_api.auth.domain.repository.UserRepository;
import com.viddefe.viddefe_api.churches.domain.model.ChurchModel;
import com.viddefe.viddefe_api.churches.infrastructure.dto.ChurchResDto;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthMeUseCase implements AuthMeService {
    private final UserRepository userRepository;
    @Override
    @Transactional(readOnly = true)
    public UserInfo getUserInfo(@NonNull UUID userId) {
        UserModel user = userRepository.findById(userId).orElseThrow(
                () -> new IllegalArgumentException("User not found")
        );
        ChurchModel church = user.getPeople().getChurch();
        System.out.println("Name: " + user.getPeople().getFirstName() + " " + user.getPeople().getLastName());
        System.out.println("Church found: " + (church != null ? church.getName() : "No church"));
        ChurchResDto churchResDto = church != null ? church.toDto() : null;
        return new UserInfo(churchResDto, user.getEmail(), user.getRolUser(), user.getPeople().toDto());
    }
}
