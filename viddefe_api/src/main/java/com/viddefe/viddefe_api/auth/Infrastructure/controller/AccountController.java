package com.viddefe.viddefe_api.auth.Infrastructure.controller;

import com.viddefe.viddefe_api.auth.Infrastructure.dto.InvitationDto;
import com.viddefe.viddefe_api.auth.contracts.AccountService;
import com.viddefe.viddefe_api.common.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth/account")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    @GetMapping("/status")
    public String accountStatus() {
        return "Account is active";
    }

    @PostMapping("/invitations")
    public ResponseEntity<ApiResponse<Void>> sendInvitation(@Valid @RequestBody InvitationDto invitationDto) {
        // Logic to send invitation
        accountService.invite(invitationDto);
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
