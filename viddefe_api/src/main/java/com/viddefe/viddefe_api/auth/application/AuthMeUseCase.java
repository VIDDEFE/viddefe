package com.viddefe.viddefe_api.auth.application;

import com.viddefe.viddefe_api.auth.Infrastructure.dto.UserInfo;
import com.viddefe.viddefe_api.auth.contracts.AuthMeService;
import com.viddefe.viddefe_api.auth.domain.model.UserModel;
import com.viddefe.viddefe_api.auth.domain.repository.UserRepository;
import com.viddefe.viddefe_api.churches.contracts.ChurchPastorService;
import com.viddefe.viddefe_api.churches.domain.model.ChurchModel;
import com.viddefe.viddefe_api.churches.infrastructure.dto.ChurchResDto;
import com.viddefe.viddefe_api.people.domain.model.PeopleModel;
import jakarta.persistence.EntityNotFoundException;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthMeUseCase implements AuthMeService {
    private final UserRepository userRepository;
    private final ChurchPastorService churchPastorService;
    @Override
    @Transactional(readOnly = true)
    public UserInfo getUserInfo(@NonNull UUID userId) {
        UserModel user = userRepository.findById(userId).orElseThrow(
                () -> new EntityNotFoundException("User not found")
        );
        ChurchModel church = user.getPeople().getChurch();
        PeopleModel pastor = churchPastorService.getPastorFromChurch(church);
        ChurchResDto churchResDto = church != null ? church.toDto() : null;
        assert churchResDto != null;
        churchResDto.setPastor(pastor != null ? pastor.toDto() : null);
        return new UserInfo(churchResDto, user.getEmail(), user.getRolUser(), user.getPeople().toDto());
    }
}
