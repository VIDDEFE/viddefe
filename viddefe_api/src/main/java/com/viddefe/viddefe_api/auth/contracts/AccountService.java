package com.viddefe.viddefe_api.auth.contracts;


import java.util.UUID;

import com.viddefe.viddefe_api.auth.infrastructure.dto.InvitationDto;

public interface AccountService {

    /** Invite a new user by sending an invitation email or in the future by whatsapp.
     *
     * @param invitationDto The invitation details including email, role, and permissions.
     */
    void invite(InvitationDto invitationDto, UUID churchId);
        /**
        * Retrieves the account ID associated with a given person ID.
        *
        * @param peopleId The unique identifier of the person whose account ID is to be retrieved.
        * @return The UUID of the account associated with the provided person ID.
        * @throws IllegalArgumentException if no account is found for the given person ID.
        */
    UUID getAccountIdByPeopleId(UUID peopleId);
}
