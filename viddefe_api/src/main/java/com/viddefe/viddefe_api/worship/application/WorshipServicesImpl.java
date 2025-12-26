package com.viddefe.viddefe_api.worship.application;

import com.viddefe.viddefe_api.worship.contracts.WorshipService;
import com.viddefe.viddefe_api.worship.domain.models.WorshipModel;
import com.viddefe.viddefe_api.worship.domain.repository.WorshipRepository;
import com.viddefe.viddefe_api.worship.infrastructure.dto.CreateWorshipDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.awt.print.Pageable;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class WorshipServicesImpl implements WorshipService {
    private final WorshipRepository worshipRepository;

    @Override
    public WorshipModel createWorship(CreateWorshipDto worshipModel) {
        return null;
    }

    @Override
    public WorshipModel getWorshipById(UUID id) {
        return null;
    }

    @Override
    public Page<WorshipModel> getAllWorships(Pageable pageable) {
        return null;
    }

    @Override
    public void deleteWorship(UUID id) {

    }
}
