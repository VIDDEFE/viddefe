package com.viddefe.viddefe_api.auth.Infrastructure.controller;

import com.viddefe.viddefe_api.auth.Infrastructure.dto.SignInDTO;
import com.viddefe.viddefe_api.auth.Infrastructure.dto.SignInResDTO;
import com.viddefe.viddefe_api.auth.Infrastructure.dto.SignUpDTO;
import com.viddefe.viddefe_api.common.response.ApiResponse;
import jakarta.servlet.http.Cookie;
import com.viddefe.viddefe_api.auth.contracts.AuthService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.net.URI;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class SignController {

    private final AuthService signService;
    private final Environment env;

    @PostMapping("/sign-up")
    public ResponseEntity<ApiResponse<String>> signUp(@Valid @RequestBody SignUpDTO signUpDTO) {
        String response = signService.signUp(signUpDTO);
        return ResponseEntity.created(URI.create("/auth/sign-up/"+response)).body(ApiResponse.ok(response));
    }

    @PostMapping("/sign-in")
    public ResponseEntity<ApiResponse<SignInResDTO>> signIn (@Valid @RequestBody SignInDTO signinDTO, HttpServletResponse response) {
        SignInResDTO signInResDTO = signService.signIn(signinDTO);
        String jwt = signService.generateJwt(signInResDTO);
        Cookie cookie = new Cookie("access_token",jwt);
        cookie.setHttpOnly(true);
        cookie.setSecure(env.matchesProfiles("prod"));
        cookie.setPath("/");
        cookie.setMaxAge(60 * 60);
        response.addCookie(cookie);
        return ResponseEntity.accepted().body(ApiResponse.ok(signInResDTO));
    }
}
