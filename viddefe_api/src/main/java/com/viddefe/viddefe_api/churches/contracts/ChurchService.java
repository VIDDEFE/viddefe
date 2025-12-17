package com.viddefe.viddefe_api.churches.contracts;

import com.viddefe.viddefe_api.churches.infrastructure.dto.ChurchDTO;
import com.viddefe.viddefe_api.churches.infrastructure.dto.ChurchDetailedResDto;
import com.viddefe.viddefe_api.churches.infrastructure.dto.ChurchResDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface ChurchService {
    ChurchResDto addChurch(ChurchDTO dto);
    ChurchResDto addChildChurch(UUID parentChurchId, ChurchDTO dto, UUID creatorPastorId);
    ChurchResDto updateChurch(UUID id, ChurchDTO dto, UUID updaterPastorId);
    void deleteChurch(UUID id);
    Page<ChurchResDto> getChildrenChurches(Pageable pageable, UUID churchId);
    ChurchDetailedResDto getChurchById(UUID id);
}

