package com.viddefe.viddefe_api.auth.infrastructure.dto;

import java.util.UUID;

import com.viddefe.viddefe_api.auth.domain.model.RolUserModel;
import com.viddefe.viddefe_api.churches.infrastructure.dto.ChurchResDto;
import com.viddefe.viddefe_api.people.infrastructure.dto.PeopleResDto;

/**
 * Record representing user information including church details, user email, role, and personal details.
 *
 * @param church   the church information
 * @param user     the user's email or contact_phone
 * @param rolUser  the user's role
 * @param person   the person's details
 * @param userId   the user's ID
 */
public record UserInfo(
        ChurchResDto church,
        String user,
        RolUserModel rolUser,
        PeopleResDto person,
        UUID userId
) {
}
