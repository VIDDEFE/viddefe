package com.viddefe.viddefe_api.auth.application;

import com.viddefe.viddefe_api.auth.Infrastructure.dto.MetadataUserDto;
import com.viddefe.viddefe_api.auth.contracts.AuthMeService;
import com.viddefe.viddefe_api.auth.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthMeUseCase implements AuthMeService {
    private final UserRepository userRepository;
    @Override
    public Map<String, Object> getMetadataUserDto() {
        return null;
    }
}
