package com.viddefe.viddefe_api.auth.application;

import com.viddefe.viddefe_api.StatesCities.domain.model.CitiesModel;
import com.viddefe.viddefe_api.StatesCities.domain.model.StatesModel;
import com.viddefe.viddefe_api.auth.Infrastructure.dto.UserInfo;
import com.viddefe.viddefe_api.auth.contracts.PermissionService;
import com.viddefe.viddefe_api.auth.domain.model.PermissionModel;
import com.viddefe.viddefe_api.auth.domain.model.RolUserModel;
import com.viddefe.viddefe_api.auth.domain.model.UserModel;
import com.viddefe.viddefe_api.auth.domain.repository.UserRepository;
import com.viddefe.viddefe_api.churches.contracts.ChurchPastorService;
import com.viddefe.viddefe_api.churches.domain.model.ChurchModel;
import com.viddefe.viddefe_api.people.domain.model.PeopleModel;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

/**
 * Tests unitarios para AuthMeUseCase.
 *
 * Valida que:
 * 1. La obtención de información del usuario funciona correctamente
 * 2. Los permisos se recuperan correctamente
 * 3. Las excepciones se manejan apropiadamente
 * 4. La arquitectura DDD se respeta (uso de servicios segregados)
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("AuthMeUseCase Tests")
class AuthMeUseCaseTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private ChurchPastorService churchPastorService;

    @Mock
    private PermissionService permissionService;

    @InjectMocks
    private AuthMeUseCase authMeUseCase;

    private UUID userId;
    private UUID churchId;
    private UserModel user;
    private PeopleModel person;
    private PeopleModel pastor;
    private ChurchModel church;
    private RolUserModel rolUser;
    private StatesModel state;
    private CitiesModel city;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        churchId = UUID.randomUUID();

        state = new StatesModel();
        state.setId(1L);
        state.setName("California");

        city = new CitiesModel();
        city.setId(1L);
        city.setName("Los Angeles");
        city.setStates(state);

        church = new ChurchModel();
        church.setId(churchId);
        church.setName("Test Church");
        church.setCity(city);

        person = new PeopleModel();
        person.setId(UUID.randomUUID());
        person.setFirstName("John");
        person.setLastName("Doe");
        person.setCc("123456789");
        person.setPhone("1234567890");
        person.setBirthDate(LocalDate.of(1990, 1, 1));
        person.setState(state);
        person.setChurch(church);

        pastor = new PeopleModel();
        pastor.setId(UUID.randomUUID());
        pastor.setFirstName("Pastor");
        pastor.setLastName("Smith");
        pastor.setCc("987654321");
        pastor.setPhone("0987654321");
        pastor.setBirthDate(LocalDate.of(1980, 5, 15));
        pastor.setState(state);

        rolUser = new RolUserModel();
        rolUser.setId(1L);
        rolUser.setName("USER");

        user = new UserModel();
        user.setId(userId);
        user.setEmail("test@test.com");
        user.setPeople(person);
        user.setRolUser(rolUser);
    }

    @Nested
    @DisplayName("getUserInfo")
    class GetUserInfo {

        @Test
        @DisplayName("Should return complete user info with church and pastor")
        void shouldReturnCompleteUserInfo() {
            // Given
            when(userRepository.findByIdWithPeopleAndChurch(userId)).thenReturn(Optional.of(user));
            when(churchPastorService.getPastorFromChurch(church)).thenReturn(pastor);

            // When
            UserInfo result = authMeUseCase.getUserInfo(userId);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.user()).isEqualTo("test@test.com");
            assertThat(result.rolUser()).isEqualTo(rolUser);
            assertThat(result.church()).isNotNull();
            assertThat(result.church().getName()).isEqualTo("Test Church");
            assertThat(result.person()).isNotNull();
        }

        @Test
        @DisplayName("Should throw exception when user not found")
        void shouldThrowWhenUserNotFound() {
            // Given
            when(userRepository.findByIdWithPeopleAndChurch(userId)).thenReturn(Optional.empty());

            // When/Then
            assertThatThrownBy(() -> authMeUseCase.getUserInfo(userId))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("User not found");
        }

        @Test
        @DisplayName("Should include pastor info in church response")
        void shouldIncludePastorInfoInChurchResponse() {
            // Given
            when(userRepository.findByIdWithPeopleAndChurch(userId)).thenReturn(Optional.of(user));
            when(churchPastorService.getPastorFromChurch(church)).thenReturn(pastor);

            // When
            UserInfo result = authMeUseCase.getUserInfo(userId);

            // Then
            assertThat(result.church().getPastor()).isNotNull();
            assertThat(result.church().getPastor().firstName()).isEqualTo("Pastor");
        }

        @Test
        @DisplayName("Should handle church without pastor")
        void shouldHandleChurchWithoutPastor() {
            // Given
            when(userRepository.findByIdWithPeopleAndChurch(userId)).thenReturn(Optional.of(user));
            when(churchPastorService.getPastorFromChurch(church)).thenReturn(null);

            // When
            UserInfo result = authMeUseCase.getUserInfo(userId);

            // Then
            assertThat(result.church()).isNotNull();
            assertThat(result.church().getPastor()).isNull();
        }

        @Test
        @DisplayName("Should return correct role information")
        void shouldReturnCorrectRoleInfo() {
            // Given
            RolUserModel adminRole = new RolUserModel();
            adminRole.setId(2L);
            adminRole.setName("ADMIN");
            user.setRolUser(adminRole);

            when(userRepository.findByIdWithPeopleAndChurch(userId)).thenReturn(Optional.of(user));
            when(churchPastorService.getPastorFromChurch(church)).thenReturn(pastor);

            // When
            UserInfo result = authMeUseCase.getUserInfo(userId);

            // Then
            assertThat(result.rolUser().getName()).isEqualTo("ADMIN");
        }

        @Test
        @DisplayName("Should throw NullPointerException when userId is null")
        void shouldThrowWhenUserIdIsNull() {
            // When/Then
            assertThatThrownBy(() -> authMeUseCase.getUserInfo(null))
                .isInstanceOf(NullPointerException.class);
        }
    }

    @Nested
    @DisplayName("getUserPermissions")
    class GetUserPermissions {

        @Test
        @DisplayName("Should return list of permission names")
        void shouldReturnPermissionNames() {
            // Given
            PermissionModel permission1 = new PermissionModel();
            permission1.setId(1L);
            permission1.setName("READ_PEOPLE");

            PermissionModel permission2 = new PermissionModel();
            permission2.setId(2L);
            permission2.setName("WRITE_PEOPLE");

            PermissionModel permission3 = new PermissionModel();
            permission3.setId(3L);
            permission3.setName("DELETE_PEOPLE");

            when(permissionService.findByUserId(userId))
                .thenReturn(List.of(permission1, permission2, permission3));

            // When
            List<String> result = authMeUseCase.getUserPermissions(userId);

            // Then
            assertThat(result).hasSize(3);
            assertThat(result).containsExactlyInAnyOrder("READ_PEOPLE", "WRITE_PEOPLE", "DELETE_PEOPLE");
        }

        @Test
        @DisplayName("Should return empty list when no permissions")
        void shouldReturnEmptyListWhenNoPermissions() {
            // Given
            when(permissionService.findByUserId(userId)).thenReturn(Collections.emptyList());

            // When
            List<String> result = authMeUseCase.getUserPermissions(userId);

            // Then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("Should throw NullPointerException when userId is null")
        void shouldThrowWhenUserIdIsNull() {
            // When/Then
            assertThatThrownBy(() -> authMeUseCase.getUserPermissions(null))
                .isInstanceOf(NullPointerException.class);
        }

        @Test
        @DisplayName("Should call permissionService with correct userId")
        void shouldCallPermissionServiceWithCorrectUserId() {
            // Given
            when(permissionService.findByUserId(userId)).thenReturn(Collections.emptyList());

            // When
            authMeUseCase.getUserPermissions(userId);

            // Then
            verify(permissionService).findByUserId(userId);
        }

        @Test
        @DisplayName("Should preserve permission order from service")
        void shouldPreservePermissionOrder() {
            // Given
            PermissionModel permission1 = new PermissionModel();
            permission1.setName("A_PERMISSION");

            PermissionModel permission2 = new PermissionModel();
            permission2.setName("B_PERMISSION");

            PermissionModel permission3 = new PermissionModel();
            permission3.setName("C_PERMISSION");

            when(permissionService.findByUserId(userId))
                .thenReturn(List.of(permission1, permission2, permission3));

            // When
            List<String> result = authMeUseCase.getUserPermissions(userId);

            // Then
            assertThat(result).containsExactly("A_PERMISSION", "B_PERMISSION", "C_PERMISSION");
        }
    }
}

