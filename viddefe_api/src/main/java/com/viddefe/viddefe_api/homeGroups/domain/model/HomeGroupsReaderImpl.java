package com.viddefe.viddefe_api.homeGroups.domain.model;

import com.viddefe.viddefe_api.homeGroups.domain.repository.HomeGroupReader;
import com.viddefe.viddefe_api.homeGroups.domain.repository.HomeGroupsRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class HomeGroupsReaderImpl implements HomeGroupReader {
    private final HomeGroupsRepository homeGroupsRepository;
    @Override
    public HomeGroupsModel findById(UUID groupId) {
        return homeGroupsRepository.findById(groupId).orElseThrow(
                () -> new EntityNotFoundException("No se encontró el grupo")
        );
    }
}
