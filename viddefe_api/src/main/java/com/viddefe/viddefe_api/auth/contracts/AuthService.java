package com.viddefe.viddefe_api.auth.contracts;

import com.viddefe.viddefe_api.auth.Infrastructure.dto.SignInDTO;
import com.viddefe.viddefe_api.auth.Infrastructure.dto.SignInResDTO;
import com.viddefe.viddefe_api.auth.Infrastructure.dto.SignUpDTO;

public interface AuthService {
    String signUp(SignUpDTO dto);
    SignInResDTO signIn(SignInDTO dto);
    String generateJwt(SignInResDTO dto);
}

