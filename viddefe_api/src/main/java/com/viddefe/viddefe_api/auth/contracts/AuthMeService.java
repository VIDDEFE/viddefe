package com.viddefe.viddefe_api.auth.contracts;

import com.viddefe.viddefe_api.auth.Infrastructure.dto.UserInfo;

import java.util.Map;
import java.util.UUID;

public interface AuthMeService {
    /**
     *
     * @param userId
     * @return UserInfo
     */
    UserInfo getUserInfo(UUID userId);
}
