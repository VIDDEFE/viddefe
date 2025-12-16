package com.viddefe.viddefe_api.churches.application;

import com.viddefe.viddefe_api.churches.domain.model.ChurchModel;
import com.viddefe.viddefe_api.churches.domain.model.ChurchPastor;
import com.viddefe.viddefe_api.churches.domain.repository.ChurchPastorRepository;
import com.viddefe.viddefe_api.churches.domain.repository.ChurchRepository;
import com.viddefe.viddefe_api.people.contracts.PeopleLookup;
import com.viddefe.viddefe_api.people.domain.model.PeopleModel;
import jakarta.persistence.EntityNotFoundException;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ChurchPastorService {
    private final ChurchPastorRepository churchPastorRepository;
    private final ChurchRepository churchRepository;
    private final PeopleLookup peopleLookup;

    @Transactional
    public ChurchPastor addPastorToChurch(@NonNull UUID pastorId, @NonNull ChurchModel church) {
        PeopleModel pastor = peopleLookup.getPeopleById(pastorId);
        pastor.setChurch(church);
        ChurchPastor churchPastor = new ChurchPastor();
        churchPastor.setPastor(pastor);
        churchPastor.setChurch(church);
        churchPastor = churchPastorRepository.save(churchPastor);
        peopleLookup.enrollPersonToChurch(pastor, church);
        return churchPastor;
    }

    public void removePastorFromChurch(@NonNull ChurchModel church) {
        ChurchPastor churchPastor = churchPastorRepository.findByChurch(church)
                .orElseThrow(() -> new EntityNotFoundException("Church has no assigned pastor"));
        PeopleModel pastor = churchPastor.getPastor();
        pastor.setChurch(null);
        churchPastorRepository.delete(churchPastor);
    }

    @Transactional(readOnly = true)
    public PeopleModel getPastorFromChurch(@NonNull ChurchModel church) {
        ChurchPastor churchPastor = churchPastorRepository.findByChurch(church)
                .orElseThrow(() -> new EntityNotFoundException("Church has no assigned pastor"));
        return churchPastor.getPastor();
    }

    public ChurchPastor changeChurchPastor(@NonNull UUID newPastorId, @NonNull ChurchModel church) {
        PeopleModel newPastor = peopleLookup.getPeopleById(newPastorId);
        ChurchPastor churchPastor = churchPastorRepository.findByChurch(church)
                .orElseThrow(() -> new EntityNotFoundException("Church has no assigned pastor"));
        PeopleModel oldPastor = churchPastor.getPastor();
        oldPastor.setChurch(null);
        peopleLookup.enrollPersonToChurch(newPastor, church);
        churchPastor.setPastor(newPastor);
        return  churchPastorRepository.save(churchPastor);
    }
}
