package com.viddefe.viddefe_api.churches;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ChurchService {
    private final ChurchRepository churchRepository;

    public ChurchModel getChurchById(UUID id){
        return churchRepository.findById(id).orElseThrow(() -> new RuntimeException("Iglesia no encontrado: " + id));
    }
}
