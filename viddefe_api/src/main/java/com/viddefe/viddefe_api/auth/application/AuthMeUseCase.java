package com.viddefe.viddefe_api.auth.application;

import com.viddefe.viddefe_api.auth.Infrastructure.dto.UserInfo;
import com.viddefe.viddefe_api.auth.contracts.AuthMeService;
import com.viddefe.viddefe_api.auth.contracts.PermissionService;
import com.viddefe.viddefe_api.auth.domain.model.PermissionModel;
import com.viddefe.viddefe_api.auth.domain.model.UserModel;
import com.viddefe.viddefe_api.auth.domain.model.UserPermissions;
import com.viddefe.viddefe_api.auth.domain.repository.UserRepository;
import com.viddefe.viddefe_api.churches.contracts.ChurchPastorService;
import com.viddefe.viddefe_api.churches.domain.model.ChurchModel;
import com.viddefe.viddefe_api.churches.infrastructure.dto.ChurchResDto;
import com.viddefe.viddefe_api.people.domain.model.PeopleModel;
import jakarta.persistence.EntityNotFoundException;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
/**
 * Service implementation for retrieving authenticated user information.
 */
@Service
@RequiredArgsConstructor
public class AuthMeUseCase implements AuthMeService {
    private final UserRepository userRepository;
    private final ChurchPastorService churchPastorService;
    private final PermissionService permissionService;
    /**
     * Retrieves user information based on the provided user ID.
     *
     * @param userId the UUID of the user
     * @return UserInfo containing church details, user email, role, and personal details
     * @throws EntityNotFoundException if the user is not found
     */
    @Override
    @Transactional(readOnly = true)
    public UserInfo getUserInfo(@NonNull UUID userId) {
        // Usa findByIdWithPeopleAndChurch para evitar N+1 queries
        UserModel user = userRepository.findByIdWithPeopleAndChurch(userId).orElseThrow(
                () -> new EntityNotFoundException("User not found")
        );
        ChurchModel church = user.getPeople().getChurch();
        PeopleModel pastor = churchPastorService.getPastorFromChurch(church);
        ChurchResDto churchResDto = church != null ? church.toDto() : null;
        assert churchResDto != null;
        churchResDto.setPastor(pastor != null ? pastor.toDto() : null);

        String contact = user.getEmail() != null && !user.getEmail().isBlank() ? user.getEmail() : user.getPhone();

        return new UserInfo(
                churchResDto,
                contact,
                user.getRolUser(),
                user.getPeople().toDto()
        );
    }

    /**
     * Retrieves a list of permissions associated with the specified user ID.
     *
     * @param userId the {@link UUID}  of the user
     * @return List of permission names
     */
    @Override
    @Transactional(readOnly = true)
    public List<String> getUserPermissions(@NonNull UUID userId) {
        List<PermissionModel> permissions = permissionService.findByUserId(userId);
        return permissions.stream()
                .map(PermissionModel::getName)
                .toList();
    }

    @Override
    public String getContactByPersonId(UUID personId) throws InterruptedException {
        UserModel user = userRepository.findByPeopleId(personId).orElseThrow(
                () -> new EntityNotFoundException("User not found for personId: " + personId)
        );
        Thread.sleep(2000); // Simulated delay of 2 seconds
        if (user.getEmail() != null && !user.getEmail().isBlank()) {
            return user.getEmail();
        } else {
            return user.getPhone();
        }
    }
}
