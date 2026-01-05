package com.viddefe.viddefe_api.auth.Infrastructure.controller;

import com.viddefe.viddefe_api.auth.Infrastructure.dto.*;
import com.viddefe.viddefe_api.auth.contracts.AuthMeService;
import com.viddefe.viddefe_api.churches.infrastructure.dto.ChurchDTO;
import com.viddefe.viddefe_api.common.response.ApiResponse;
import com.viddefe.viddefe_api.common.Components.JwtUtil;
import com.viddefe.viddefe_api.people.infrastructure.dto.PeopleDTO;
import com.viddefe.viddefe_api.people.infrastructure.dto.PeopleResDto;
import jakarta.servlet.http.Cookie;
import com.viddefe.viddefe_api.auth.contracts.AuthService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class SignController {

    private final AuthService signService;
    private final AuthMeService authMeService;
    private final Environment env;
    private final JwtUtil jwtUtil;

    @PostMapping("/sign-up/user")
    public ResponseEntity<ApiResponse<AuthProcessResponse<String>>> signUpUser(@Valid @RequestBody SignUpDTO signUpDTO) {
        AuthProcessResponse<String> response = signService.signUp(signUpDTO);
        return ResponseEntity.created(URI.create("/auth/sign-up/"+response)).body(ApiResponse.ok(response));
    }

    @PostMapping("/sign-up/pastor")
    public ResponseEntity<ApiResponse<AuthProcessResponse<PeopleResDto>>> signUpPastor(@Valid @RequestBody PeopleDTO dto) {
        AuthProcessResponse<PeopleResDto> response = signService.registerPastor(dto);
        return ResponseEntity.created(URI.create("/auth/sign-up/"+response)).body(ApiResponse.ok(response));
    }

    @PostMapping("/sign-up/church")
    public ResponseEntity<ApiResponse<AuthProcessResponse<Void>>> signUpChurch(@Valid @RequestBody ChurchDTO churchDTO) {
        AuthProcessResponse<Void> response = signService.registerChurch(churchDTO);
        return new ResponseEntity<>(ApiResponse.created(response), HttpStatus.CREATED);
    }

    @PostMapping("/sign-in")
    public ResponseEntity<ApiResponse<AuthProcessResponse<SignInResDTO>>> signIn (@Valid @RequestBody SignInDTO signinDTO, HttpServletResponse response) {
        AuthProcessResponse<SignInResDTO> responseSign = signService.signIn(signinDTO);
        List<String> permissions = authMeService.getUserPermissions(responseSign.getData().getUserId());
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("permissions", permissions);
        String jwt = signService.generateJwt(responseSign.getData(), permissions);
        Cookie cookie = new Cookie("access_token",jwt);
        cookie.setHttpOnly(true);
        cookie.setSecure(env.matchesProfiles("prod"));
        cookie.setPath("/");
        cookie.setMaxAge(60 * 60);
        response.addCookie(cookie);
        return new ResponseEntity<>(ApiResponse.ok(responseSign).withMeta(metadata), HttpStatus.ACCEPTED);
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserInfo>> me (@CookieValue(name = "access_token") String jwt) {
        UUID userId = jwtUtil.getUserId(jwt);
        UserInfo userInfo = authMeService.getUserInfo(userId);
        return ResponseEntity.ok(ApiResponse.ok(userInfo));
    }
}
