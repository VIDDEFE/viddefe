package com.viddefe.viddefe_api.auth.application;

import com.viddefe.viddefe_api.auth.contracts.AuthService;
import com.viddefe.viddefe_api.auth.Infrastructure.dto.SignInDTO;
import com.viddefe.viddefe_api.auth.Infrastructure.dto.SignInResDTO;
import com.viddefe.viddefe_api.auth.Infrastructure.dto.SignUpDTO;
import com.viddefe.viddefe_api.auth.domain.model.RolUserModel;
import com.viddefe.viddefe_api.auth.domain.model.UserModel;
import com.viddefe.viddefe_api.auth.domain.repository.UserRepository;
import com.viddefe.viddefe_api.common.exception.CustomExceptions;
import com.viddefe.viddefe_api.config.Components.JwtUtil;
import com.viddefe.viddefe_api.people.contracts.PeopleService;
import com.viddefe.viddefe_api.people.domain.model.PeopleModel;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final PasswordEncoder passwordEncoder;
    private final RolesUserService rolesUserService;
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final PeopleService peopleService;
    @Override
    public String signUp(SignUpDTO dto) {
        userRepository.findByEmail(dto.getEmail())
                .ifPresent(u -> {
                    throw new CustomExceptions.ResourceAlreadyExistsException(
                            "User with email " + dto.getEmail() + " already exists"
                    );
                });
        Long roleId = dto.getRoleId() != null ? dto.getRoleId() : 2L;
        // Crear y guardar el nuevo usuario
        UserModel userModel = new UserModel();
        RolUserModel rolUserModel = rolesUserService.foundRolUserById(roleId);
        PeopleModel peopleModel = peopleService.getPeopleById(dto.getPeopleId());
        userModel.setPeople(peopleModel);
        userModel.setPassword(passwordEncoder.encode(dto.getPassword()));
        userModel.setEmail(dto.getEmail());
        userModel.setRolUser(rolUserModel);

        userRepository.save(userModel);

        return userModel.getPeople().getId().toString();
    }

    @Override
    public SignInResDTO signIn(SignInDTO dto) {
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

    @Override
    public String generateJwt(SignInResDTO dto) {
        return jwtUtil.generateToken(dto.getEmail(),
                dto.getRolUserModel().getName(),
                dto.getFullName(),
                dto.getPersonId()
        );
    }
}

