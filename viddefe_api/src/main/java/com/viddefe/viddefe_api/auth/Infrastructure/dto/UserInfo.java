package com.viddefe.viddefe_api.auth.Infrastructure.dto;

import com.viddefe.viddefe_api.auth.domain.model.RolUserModel;
import com.viddefe.viddefe_api.churches.infrastructure.dto.ChurchResDto;
import com.viddefe.viddefe_api.people.infrastructure.dto.PeopleResDto;

public record UserInfo(ChurchResDto church, String user, RolUserModel rolUser, PeopleResDto person) {
}
