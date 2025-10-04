package com.viddefe.viddefe_api.auth.service;

import com.viddefe.viddefe_api.auth.dto.SignInDTO;
import com.viddefe.viddefe_api.auth.dto.SignInResDTO;
import com.viddefe.viddefe_api.auth.dto.SignUpDTO;
import com.viddefe.viddefe_api.auth.model.UserModel;
import com.viddefe.viddefe_api.auth.repository.UserRepository;
import com.viddefe.viddefe_api.common.exception.CustomExceptions;
import com.viddefe.viddefe_api.config.Components.JwtUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SignService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public String singUp(@Valid SignUpDTO dto) {
        UserModel userModel = userRepository.findById(dto.getPeopleId())
                .orElseThrow(() -> new CustomExceptions.ResourceNotFoundException("User not found"));
        userModel.setPassword(passwordEncoder.encode(dto.getPassword()));
        userModel.setEmail(dto.getEmail());
        userRepository.save(userModel);
        return userModel.getPeople().getId().toString();
    }

    public SignInResDTO signIn(@Valid SignInDTO dto) {
        UserModel userBd = userRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new CustomExceptions.ResourceNotFoundException("User not found by email" + dto.getEmail()));
        if(!passwordEncoder.matches(dto.getPassword(), userBd.getPassword())) {
            throw new CustomExceptions.InvalidCredentialsException("Wrong password");
        }
        return new SignInResDTO(userBd.getEmail(),
                userBd.getRolUser(),
                userBd.getPeople().getFirstName() + " " + userBd.getPeople().getLastName(),
                userBd.getPeople().getId()
        );
    }

    public String generateJwt(SignInResDTO dto) {
        return jwtUtil.generateToken(dto.getEmail(),
                dto.getRolUserModel().getName(),
                dto.getFullName(),
                dto.getPersonId()
        );
    }
}
