package com.viddefe.viddefe_api.auth.contracts;


import com.viddefe.viddefe_api.auth.Infrastructure.dto.InvitationDto;

import java.util.UUID;

public interface AccountService {

    /** Invite a new user by sending an invitation email or in the future by whatsapp.
     *
     * @param invitationDto The invitation details including email, role, and permissions.
     */
    void invite(InvitationDto invitationDto, UUID churchId);
}
