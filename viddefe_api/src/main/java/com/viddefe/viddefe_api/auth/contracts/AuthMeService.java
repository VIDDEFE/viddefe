package com.viddefe.viddefe_api.auth.contracts;

import com.viddefe.viddefe_api.auth.Infrastructure.dto.MetadataUserDto;

import java.util.Map;

public interface AuthMeService {
    Map<String, Object> getMetadataUserDto();
}
