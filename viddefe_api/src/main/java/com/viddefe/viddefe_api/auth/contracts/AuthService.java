package com.viddefe.viddefe_api.auth.contracts;

import com.viddefe.viddefe_api.auth.Infrastructure.dto.AuthProcessResponse;
import com.viddefe.viddefe_api.auth.Infrastructure.dto.SignInDTO;
import com.viddefe.viddefe_api.auth.Infrastructure.dto.SignInResDTO;
import com.viddefe.viddefe_api.auth.Infrastructure.dto.SignUpDTO;
import com.viddefe.viddefe_api.churches.infrastructure.dto.ChurchDTO;
import com.viddefe.viddefe_api.people.infrastructure.dto.PeopleDTO;
import com.viddefe.viddefe_api.people.infrastructure.dto.PeopleResDto;

public interface AuthService {
    AuthProcessResponse<String> signUp(SignUpDTO dto);
    AuthProcessResponse<PeopleResDto> registerPastor(PeopleDTO peopleDTO);
    AuthProcessResponse<Void> registerChurch(ChurchDTO dto);
    AuthProcessResponse<SignInResDTO> signIn(SignInDTO dto);
    String generateJwt(SignInResDTO dto);
}

