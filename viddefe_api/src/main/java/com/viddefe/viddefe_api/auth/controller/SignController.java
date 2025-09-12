package com.viddefe.viddefe_api.auth.controller;

import com.viddefe.viddefe_api.auth.dto.SignInDTO;
import com.viddefe.viddefe_api.auth.dto.SignInResDTO;
import com.viddefe.viddefe_api.auth.dto.SignUpDTO;
import jakarta.servlet.http.Cookie;
import com.viddefe.viddefe_api.auth.service.SignService;
import jakarta.servlet.http.HttpServletResponse;
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

    private final SignService signService;
    private final Environment env;

    @PostMapping("/sign-up")
    public ResponseEntity<String> signUp(@RequestBody SignUpDTO signUpDTO) {
        String response = signService.singUp(signUpDTO);
        return ResponseEntity.created(URI.create("/auth/sign-up/"+response)).body("Sign up successful");
    }

    @PostMapping("/sign-in")
    public ResponseEntity<SignInResDTO> signIn(@RequestBody SignInDTO signinDTO, HttpServletResponse response) {
        SignInResDTO signInResDTO = signService.signIn(signinDTO);
        String jwt = signService.generateJwt(signInResDTO);
        Cookie cookie = new Cookie("access_token",jwt);
        cookie.setHttpOnly(true);
        cookie.setSecure(env.matchesProfiles("prod"));
        cookie.setPath("/");
        cookie.setMaxAge(60 * 60);
        response.addCookie(cookie);
        return ResponseEntity.accepted().body(signInResDTO);
    }
}
