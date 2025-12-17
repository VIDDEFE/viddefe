package com.viddefe.viddefe_api.auth.contracts;

import com.viddefe.viddefe_api.auth.Config.AuthFlowPastorEnum;
import com.viddefe.viddefe_api.auth.Infrastructure.dto.SignInDTO;
import com.viddefe.viddefe_api.auth.Infrastructure.dto.SignInResDTO;
import com.viddefe.viddefe_api.auth.Infrastructure.dto.SignUpDTO;
import com.viddefe.viddefe_api.people.infrastructure.dto.PeopleDTO;
import com.viddefe.viddefe_api.people.infrastructure.dto.PeopleResDto;

public interface AuthService {
    String signUp(SignUpDTO dto);
    PeopleResDto registerPastor(PeopleDTO peopleDTO);
    SignInResDTO signIn(SignInDTO dto);
    String generateJwt(SignInResDTO dto);
}

