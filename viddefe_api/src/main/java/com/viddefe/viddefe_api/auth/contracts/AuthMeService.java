package com.viddefe.viddefe_api.auth.contracts;

import java.util.List;
import java.util.UUID;

import com.viddefe.viddefe_api.auth.infrastructure.dto.UserInfo;

/**
 * Service interface for retrieving authenticated user information.
 */
public interface AuthMeService {
    /**
     *
     * @param userId {@link UUID}
     * @return UserInfo
     */
    UserInfo getUserInfo(UUID userId);

    /**
     *
     * @param  userId {@link UUID}
     * @return list of permissions
     */
    List<String> getUserPermissions(UUID userId);

    /**
     *
     * @param personId {@link UUID}
     * @return contact string {EMAIL or PHONE}
     */
    String getContactByPersonId(UUID personId) throws InterruptedException;
}
