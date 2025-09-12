package com.viddefe.viddefe_api.auth.service;

import com.viddefe.viddefe_api.auth.dto.SignUpDTO;
import com.viddefe.viddefe_api.auth.model.UserModel;
import com.viddefe.viddefe_api.auth.repository.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SignService {
    private final UserRepository userRepository;

    public String singUp(@Valid SignUpDTO dto) {
        UserModel userModel = new UserModel();
        userModel.setId(dto.getPeopleId());

        userRepository.save(userModel);
        return "Logeado";
    }
}
