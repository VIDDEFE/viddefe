package com.viddefe.viddefe_api.churches.application;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.viddefe.viddefe_api.churches.contracts.ChurchMemberShip;
import com.viddefe.viddefe_api.churches.domain.repository.ChurchMemberShipRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ChurchMemberShipImpl implements ChurchMemberShip{
 
    private final ChurchMemberShipRepository churchMemberShipRepository;

    @Override
    public List<UUID> getPeopleIdsByChurchId(UUID churchId) {
        return churchMemberShipRepository.findPeopleIdsByChurchId(churchId);
    }
}
