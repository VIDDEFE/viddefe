package com.viddefe.viddefe_api.churches.contracts;

import com.viddefe.viddefe_api.churches.infrastructure.dto.ChurchDTO;
import com.viddefe.viddefe_api.churches.domain.model.ChurchModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface ChurchService {
    ChurchModel addChurch(ChurchDTO dto);
    Page<ChurchModel> getChurches(Pageable pageable);
    ChurchModel getChurchById(UUID id);
}

