package com.viddefe.viddefe_api.homeGroups.application;

import com.viddefe.viddefe_api.homeGroups.domain.repository.RolesPeopleStrategiesRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RolesPeopleStrategesServiceImpl {
    private final RolesPeopleStrategiesRepository rolesPeopleStrategiesRepository;

}
