package com.viddefe.viddefe_api.auth.infrastructure.controller;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.viddefe.viddefe_api.auth.contracts.AccountService;
import com.viddefe.viddefe_api.auth.infrastructure.dto.InvitationDto;
import com.viddefe.viddefe_api.common.components.JwtUtil;
import com.viddefe.viddefe_api.common.response.ApiResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/auth/account")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;
    private final JwtUtil jwtUtil;

    @GetMapping("/status")
    public String accountStatus() {
        return "Account is active";
    }

    @PostMapping("/invitations")
    public ResponseEntity<ApiResponse<Void>> sendInvitation(
            @Valid @RequestBody InvitationDto invitationDto,
            @CookieValue("access_token") String accessToken
    ) {
        UUID churchId = jwtUtil.getChurchId(accessToken);
        // Logic to send invitation
        accountService.invite(invitationDto, churchId);
        return ResponseEntity.ok(ApiResponse.ok(null));
    }

    @PostMapping("/activate")
    public ResponseEntity<ApiResponse<Void>> activateAccount() {
        // Logic to activate account
        return ResponseEntity.ok(ApiResponse.ok(null));
    }

    @PostMapping("/password/reset")
    public ResponseEntity<ApiResponse<Void>> resetPassword() {
        // Logic to reset password
        return ResponseEntity.ok(ApiResponse.ok(null));
    }
}
