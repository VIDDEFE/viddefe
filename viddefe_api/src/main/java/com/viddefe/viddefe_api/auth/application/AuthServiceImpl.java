package com.viddefe.viddefe_api.auth.application;

import com.viddefe.viddefe_api.auth.Config.AuthFlowPastorEnum;
import com.viddefe.viddefe_api.auth.Infrastructure.dto.AuthProcessResponse;
import com.viddefe.viddefe_api.auth.contracts.AuthService;
import com.viddefe.viddefe_api.auth.Infrastructure.dto.SignInDTO;
import com.viddefe.viddefe_api.auth.Infrastructure.dto.SignInResDTO;
import com.viddefe.viddefe_api.auth.Infrastructure.dto.SignUpDTO;
import com.viddefe.viddefe_api.auth.domain.model.RolUserModel;
import com.viddefe.viddefe_api.auth.domain.model.UserModel;
import com.viddefe.viddefe_api.auth.domain.repository.UserRepository;
import com.viddefe.viddefe_api.churches.contracts.ChurchService;
import com.viddefe.viddefe_api.churches.infrastructure.dto.ChurchDTO;
import com.viddefe.viddefe_api.common.exception.CustomExceptions;
import com.viddefe.viddefe_api.common.Components.JwtUtil;
import com.viddefe.viddefe_api.people.contracts.PeopleReader;
import com.viddefe.viddefe_api.people.contracts.PeopleWriter;
import com.viddefe.viddefe_api.people.domain.model.PeopleModel;
import com.viddefe.viddefe_api.people.infrastructure.dto.PeopleDTO;
import com.viddefe.viddefe_api.people.infrastructure.dto.PeopleResDto;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Servicio de autenticación refactorizado.
 * 
 * Usa las nuevas interfaces segregadas:
 * - PeopleReader: Para consultas de personas (solo lectura)
 * - PeopleWriter: Para crear nuevas personas
 * 
 * Esto elimina el acoplamiento con ChurchLookup indirecto que existía
 * a través de PeopleLookup.
 */
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final PasswordEncoder passwordEncoder;
    private final RolesUserService rolesUserService;
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final PeopleReader peopleReader;
    private final PeopleWriter peopleWriter;
    private final ChurchService churchService;

    @Override
    public AuthProcessResponse<String> signUp(SignUpDTO dto) {
        userRepository.findByEmail(dto.getEmail())
                .ifPresent(u -> {
                    throw new DataIntegrityViolationException(
                            "User with email " + dto.getEmail() + " already exists"
                    );
                });
        Long roleId = dto.getRoleId() != null ? dto.getRoleId() : 2L;
        
        // Crear y guardar el nuevo usuario
        UserModel userModel = new UserModel();
        RolUserModel rolUserModel = rolesUserService.foundRolUserById(roleId);
        PeopleModel peopleModel = peopleReader.getPeopleById(dto.getPeopleId());
        userModel.setPeople(peopleModel);
        userModel.setPassword(passwordEncoder.encode(dto.getPassword()));
        userModel.setEmail(dto.getEmail());
        userModel.setRolUser(rolUserModel);

        userRepository.save(userModel);
        String id = userModel.getId().toString();
        return AuthProcessResponse.pending(AuthFlowPastorEnum.CREATION_CHURCH,id);
    }

    @Override
    public AuthProcessResponse<PeopleResDto> registerPastor(PeopleDTO peopleDTO) {
       PeopleModel pastor = peopleReader.getPastorByCcWithoutChurch(peopleDTO.getCc()).orElseGet(
                () -> peopleWriter.createPerson(peopleDTO)
       );
        return AuthProcessResponse.pending(AuthFlowPastorEnum.CREATION_USER,pastor.toDto());
    }

    @Override
    public AuthProcessResponse<Void> registerChurch(ChurchDTO dto) {
        churchService.addChurch(dto);
        return AuthProcessResponse.completed(null);
    }

    @Override
    @Transactional(readOnly = true)
    public AuthProcessResponse<SignInResDTO> signIn(SignInDTO dto) {
        UserModel user = userRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new EntityNotFoundException("User not found by email: " + dto.getEmail()));
        if(!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            throw new CustomExceptions.InvalidCredentialsException("Wrong password");
        }


        PeopleModel person = user.getPeople();
        SignInResDTO dtoRes = new SignInResDTO(
                user.getEmail(),
                user.getRolUser(),
                person.getFirstName(),
                person.getLastName(),
                person.getId(),
                user.getId()
        );

        if(person.getChurch() == null){
            System.out.println("Heyyy");
            return AuthProcessResponse.pending(AuthFlowPastorEnum.CREATION_CHURCH,dtoRes);
        }

        return AuthProcessResponse.completed(
                dtoRes
        );
    }

    @Override
    public String generateJwt(SignInResDTO dto) {
        return jwtUtil.generateToken(
                dto.getEmail(),
                dto.getRolUserModel().getName(),
                dto.getFirstName(),
                dto.getLastName(),
                dto.getUserId()
        );
    }

}
