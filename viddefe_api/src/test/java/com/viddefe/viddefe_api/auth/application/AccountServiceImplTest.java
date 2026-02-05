package com.viddefe.viddefe_api.auth.application;

import com.viddefe.viddefe_api.auth.Infrastructure.dto.InvitationDto;
import com.viddefe.viddefe_api.auth.contracts.PermissionService;
import com.viddefe.viddefe_api.auth.domain.model.PermissionModel;
import com.viddefe.viddefe_api.auth.domain.model.RolUserModel;
import com.viddefe.viddefe_api.auth.domain.model.UserModel;
import com.viddefe.viddefe_api.auth.domain.repository.UserRepository;
import com.viddefe.viddefe_api.churches.domain.model.ChurchModel;
import com.viddefe.viddefe_api.notifications.contracts.NotificationEventPublisher;
import com.viddefe.viddefe_api.people.contracts.PeopleReader;
import com.viddefe.viddefe_api.people.domain.model.PeopleModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AccountServiceImpl Tests")
class AccountServiceImplTest {

    @Mock
    private PeopleReader peopleReader;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private RolesUserService rolesUserService;

    @Mock
    private PermissionService permissionService;

    @Mock
    private NotificationEventPublisher notificationEventPublisher;

    @InjectMocks
    private AccountServiceImpl accountService;

    private InvitationDto invitationDto;
    private UUID churchId;
    private UUID personId;
    private PeopleModel person;
    private RolUserModel role;
    private ChurchModel church;

    @BeforeEach
    void setUp() {
        churchId = UUID.randomUUID();
        personId = UUID.randomUUID();

        church = new ChurchModel();
        church.setId(churchId);

        person = new PeopleModel();
        person.setId(personId);
        person.setFirstName("John");
        person.setLastName("Doe");
        person.setChurch(church);

        role = new RolUserModel();
        role.setId(1L);
        role.setName("ADMIN");

        invitationDto = new InvitationDto();
        invitationDto.setPersonId(personId);
        invitationDto.setRole(1L);
        invitationDto.setEmail("john@example.com");
        invitationDto.setPhone("1234567890");
        invitationDto.setChannel("EMAIL");
        invitationDto.setPermissions(List.of("READ", "WRITE"));
    }

    @Nested
    @DisplayName("invite Tests")
    class InviteTests {

        @Test
        @DisplayName("Should throw exception when email already exists")
        void shouldThrowWhenEmailAlreadyExists() {
            when(userRepository.existsByEmail("john@example.com")).thenReturn(true);

            assertThatThrownBy(() -> accountService.invite(invitationDto, churchId))
                    .isInstanceOf(DataIntegrityViolationException.class)
                    .hasMessageContaining("User with email already exists");
        }

        @Test
        @DisplayName("Should throw exception when phone already exists")
        void shouldThrowWhenPhoneAlreadyExists() {
            invitationDto.setEmail(null);
            when(userRepository.existsByPhone("1234567890")).thenReturn(true);

            assertThatThrownBy(() -> accountService.invite(invitationDto, churchId))
                    .isInstanceOf(DataIntegrityViolationException.class)
                    .hasMessageContaining("User with phone number already exists");
        }

        @Test
        @DisplayName("Should throw exception when user for person already exists in church")
        void shouldThrowWhenUserForPersonAlreadyExistsInChurch() {
            when(userRepository.existsByEmail(anyString())).thenReturn(false);
            when(userRepository.existsByPhone(anyString())).thenReturn(false);
            when(peopleReader.getPeopleById(personId)).thenReturn(person);
            when(userRepository.existsUserByPeopleIdAndPeopleChurchId(personId, churchId)).thenReturn(true);

            assertThatThrownBy(() -> accountService.invite(invitationDto, churchId))
                    .isInstanceOf(DataIntegrityViolationException.class)
                    .hasMessageContaining("User for the selected person already exists in the church");
        }

        @Test
        @DisplayName("Should create user and send notification when valid")
        void shouldCreateUserAndSendNotification() {
            when(userRepository.existsByEmail(anyString())).thenReturn(false);
            when(userRepository.existsByPhone(anyString())).thenReturn(false);
            when(peopleReader.getPeopleById(personId)).thenReturn(person);
            when(userRepository.existsUserByPeopleIdAndPeopleChurchId(personId, churchId)).thenReturn(false);
            when(permissionService.findByListNames(any())).thenReturn(List.of(new PermissionModel()));
            when(rolesUserService.foundRolUserById(1L)).thenReturn(role);
            when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
            when(userRepository.save(any(UserModel.class))).thenAnswer(inv -> inv.getArgument(0));

            accountService.invite(invitationDto, churchId);

            verify(userRepository).save(any(UserModel.class));
            verify(notificationEventPublisher).publish(any());
        }

        @Test
        @DisplayName("Should assign role to user")
        void shouldAssignRoleToUser() {
            when(userRepository.existsByEmail(anyString())).thenReturn(false);
            when(userRepository.existsByPhone(anyString())).thenReturn(false);
            when(peopleReader.getPeopleById(personId)).thenReturn(person);
            when(userRepository.existsUserByPeopleIdAndPeopleChurchId(personId, churchId)).thenReturn(false);
            when(permissionService.findByListNames(any())).thenReturn(List.of());
            when(rolesUserService.foundRolUserById(1L)).thenReturn(role);
            when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
            when(userRepository.save(any(UserModel.class))).thenAnswer(inv -> inv.getArgument(0));

            accountService.invite(invitationDto, churchId);

            verify(rolesUserService).foundRolUserById(1L);
        }
    }
}

