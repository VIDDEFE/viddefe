package com.viddefe.viddefe_api.churches.contracts;

import com.viddefe.viddefe_api.churches.infrastructure.dto.ChurchDTO;
import com.viddefe.viddefe_api.churches.domain.model.ChurchModel;
import com.viddefe.viddefe_api.churches.infrastructure.dto.ChurchResDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface ChurchService {
    ChurchResDto addChurch(ChurchDTO dto, UUID creatorPastorId);
    Page<ChurchResDto> getChurches(Pageable pageable);
    ChurchModel getChurchById(UUID id);
}

