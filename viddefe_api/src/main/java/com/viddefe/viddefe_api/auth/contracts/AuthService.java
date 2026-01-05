package com.viddefe.viddefe_api.auth.contracts;

import com.viddefe.viddefe_api.auth.Infrastructure.dto.AuthProcessResponse;
import com.viddefe.viddefe_api.auth.Infrastructure.dto.SignInDTO;
import com.viddefe.viddefe_api.auth.Infrastructure.dto.SignInResDTO;
import com.viddefe.viddefe_api.auth.Infrastructure.dto.SignUpDTO;
import com.viddefe.viddefe_api.churches.infrastructure.dto.ChurchDTO;
import com.viddefe.viddefe_api.people.infrastructure.dto.PeopleDTO;
import com.viddefe.viddefe_api.people.infrastructure.dto.PeopleResDto;

import java.util.List;

public interface AuthService {
    /**
     *This method recollect the data necessary to create a new user in the system.
     * And returns the user ID upon successful registration...
     * This method is used for pastor who want to register in the system. for that reason
     * the role is set to ROLE_PASTOR by default and has every permission assigned.
     * @param dto
     * @return AuthProcessResponse with the user ID if successful
     *
     */
    AuthProcessResponse<String> signUp(SignUpDTO dto);
    /**
     *
     * @param peopleDTO
     * @return AuthProcessResponse with the PeopleResDto if successful
     */
    AuthProcessResponse<PeopleResDto> registerPastor(PeopleDTO peopleDTO);
    /**
     *
     * @param dto
     * @return AuthProcessResponse with no content if successful
     */
    AuthProcessResponse<Void> registerChurch(ChurchDTO dto);
    /**
     *
     * @param dto
     * @return AuthProcessResponse with SignInResDTO if successful
     */
    AuthProcessResponse<SignInResDTO> signIn(SignInDTO dto);
    /**
     *
     * @param dto
     * @return JWT token as String
     */
    String generateJwt(SignInResDTO dto, List<String> permissions);
}

