package com.viddefe.viddefe_api.auth.contracts;

import com.viddefe.viddefe_api.auth.Infrastructure.dto.UserInfo;

import java.util.Map;
import java.util.UUID;

public interface AuthMeService {
    UserInfo getUserInfo(UUID userId);
}
