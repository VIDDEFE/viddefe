package com.viddefe.viddefe_api.auth.application;

import com.viddefe.viddefe_api.auth.domain.model.RolUserModel;
import com.viddefe.viddefe_api.auth.domain.repository.RolUserRepository;
import com.viddefe.viddefe_api.common.exception.CustomExceptions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

/**
 * Tests unitarios para RolesUserService.
 *
 * Valida que:
 * 1. La búsqueda de roles funciona correctamente
 * 2. Las excepciones se manejan apropiadamente
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("RolesUserService Tests")
class RolesUserServiceTest {

    @Mock
    private RolUserRepository rolUserRepository;

    @InjectMocks
    private RolesUserService rolesUserService;

    private RolUserModel userRole;
    private RolUserModel adminRole;
    private RolUserModel pastorRole;

    @BeforeEach
    void setUp() {
        userRole = new RolUserModel();
        userRole.setId(1L);
        userRole.setName("USER");

        adminRole = new RolUserModel();
        adminRole.setId(2L);
        adminRole.setName("ADMIN");

        pastorRole = new RolUserModel();
        pastorRole.setId(3L);
        pastorRole.setName("PASTOR");
    }

    @Nested
    @DisplayName("foundRolUserById")
    class FoundRolUserById {

        @Test
        @DisplayName("Should return USER role when found")
        void shouldReturnUserRoleWhenFound() {
            // Given
            when(rolUserRepository.findById(1L)).thenReturn(Optional.of(userRole));

            // When
            RolUserModel result = rolesUserService.foundRolUserById(1L);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(1L);
            assertThat(result.getName()).isEqualTo("USER");
        }

        @Test
        @DisplayName("Should return ADMIN role when found")
        void shouldReturnAdminRoleWhenFound() {
            // Given
            when(rolUserRepository.findById(2L)).thenReturn(Optional.of(adminRole));

            // When
            RolUserModel result = rolesUserService.foundRolUserById(2L);

            // Then
            assertThat(result.getName()).isEqualTo("ADMIN");
        }

        @Test
        @DisplayName("Should return PASTOR role when found")
        void shouldReturnPastorRoleWhenFound() {
            // Given
            when(rolUserRepository.findById(3L)).thenReturn(Optional.of(pastorRole));

            // When
            RolUserModel result = rolesUserService.foundRolUserById(3L);

            // Then
            assertThat(result.getName()).isEqualTo("PASTOR");
        }

        @Test
        @DisplayName("Should throw exception when role not found")
        void shouldThrowWhenRoleNotFound() {
            // Given
            when(rolUserRepository.findById(999L)).thenReturn(Optional.empty());

            // When/Then
            assertThatThrownBy(() -> rolesUserService.foundRolUserById(999L))
                .isInstanceOf(CustomExceptions.ResourceNotFoundException.class)
                .hasMessageContaining("Rol User not found");
        }

        @Test
        @DisplayName("Should call repository with correct ID")
        void shouldCallRepositoryWithCorrectId() {
            // Given
            when(rolUserRepository.findById(1L)).thenReturn(Optional.of(userRole));

            // When
            rolesUserService.foundRolUserById(1L);

            // Then
            verify(rolUserRepository).findById(1L);
            verify(rolUserRepository, times(1)).findById(any());
        }
    }
}

