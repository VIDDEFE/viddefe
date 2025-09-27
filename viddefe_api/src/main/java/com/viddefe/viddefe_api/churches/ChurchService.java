package com.viddefe.viddefe_api.churches;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Validated
public class ChurchService {
    private final ChurchRepository churchRepository;

    public ChurchModel addChurch(ChurchDTO dto){
        ChurchModel churchModel = ChurchModel.fromDto(dto);
        return churchRepository.save(churchModel);
    }

    public ChurchModel getChurchById(UUID id){
        return churchRepository.findById(id).orElseThrow(() -> new RuntimeException("Iglesia no encontrado: " + id));
    }
}
