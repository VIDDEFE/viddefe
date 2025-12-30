package com.viddefe.viddefe_api.worship_meetings.application;

import com.viddefe.viddefe_api.worship_meetings.contracts.TypesWorshipMeetingReader;
import com.viddefe.viddefe_api.worship_meetings.domain.models.WorshipMeetingTypes;
import com.viddefe.viddefe_api.worship_meetings.domain.repository.WorshipTypesRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TypesWorshipMeetingReaderImpl implements TypesWorshipMeetingReader {
    private final WorshipTypesRepository worshipTypesRepository;

    @Override
    public WorshipMeetingTypes getWorshipMeetingTypesById(Long id) {
        return worshipTypesRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Worship Meeting Type not found")
        );
    }

    @Override
    public List<WorshipMeetingTypes> getAllWorshipMeetingTypes() {
        return worshipTypesRepository.findAll();
    }
}
