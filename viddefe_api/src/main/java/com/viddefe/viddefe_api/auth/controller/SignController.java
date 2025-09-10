package com.viddefe.viddefe_api.auth.controller;

import com.viddefe.viddefe_api.auth.dto.SignUpDTO;
import com.viddefe.viddefe_api.auth.model.UserModel;
import com.viddefe.viddefe_api.auth.service.SignService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class SignController {
    private final SignService signService;

    @PostMapping
    public ResponseEntity<UserModel> signUp(@Validated SignUpDTO signUpDTO) {
        return ResponseEntity.created(URI.create("/auth/"+1)).body(new UserModel());
    }
}
