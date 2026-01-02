package com.viddefe.viddefe_api.auth.application;

import com.viddefe.viddefe_api.auth.Infrastructure.dto.InvitationDto;
import com.viddefe.viddefe_api.auth.contracts.AccountService;
import com.viddefe.viddefe_api.auth.contracts.PermissionService;
import com.viddefe.viddefe_api.auth.domain.model.PermissionModel;
import com.viddefe.viddefe_api.auth.domain.model.RolUserModel;
import com.viddefe.viddefe_api.auth.domain.model.UserModel;
import com.viddefe.viddefe_api.auth.domain.model.UserPermissions;
import com.viddefe.viddefe_api.auth.domain.repository.UserRepository;
import com.viddefe.viddefe_api.notifications.Infrastructure.dto.NotificationDto;
import com.viddefe.viddefe_api.notifications.Infrastructure.factory.NotificatorFactory;
import com.viddefe.viddefe_api.notifications.config.Channels;
import com.viddefe.viddefe_api.notifications.contracts.Notificator;
import com.viddefe.viddefe_api.people.contracts.PeopleReader;
import com.viddefe.viddefe_api.people.domain.model.PeopleModel;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.*;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {
    private static final String UPPER = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String LOWER = "abcdefghijklmnopqrstuvwxyz";
    private static final String DIGITS = "0123456789";
    private static final String SPECIAL = "!@#$%^&*()-_=+[]{}";
    private static final String ALL = UPPER + LOWER + DIGITS + SPECIAL;

    private final PeopleReader peopleReader;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final NotificatorFactory notificatorFactory;
    private final RolesUserService rolesUserService;
    private final PermissionService permissionService;

    @Override
    public void invite(InvitationDto dtp) {
        // Implementation goes here
        if(userRepository.existsByEmail(dtp.getEmail())) {
            throw new DataIntegrityViolationException("User with email already exists");
        }
        System.out.println("Permissions to assign: " + dtp.getPermissions());
        List<PermissionModel> permissionModels =permissionService.findByListNames(dtp.getPermissions());

        RolUserModel role = rolesUserService.foundRolUserById(dtp.getRole());
        PeopleModel person = peopleReader.getPeopleById(dtp.getPersonId());
        UserModel userModel = new UserModel();
        userModel.setEmail(dtp.getEmail());
        userModel.setPeople(person);
        userModel.setRolUser(role);
        List<UserPermissions> userPermissions = getUserPermissionsCollection(userModel, permissionModels);
        userModel.addListPermission(userPermissions);
        String temporaryPassword = generateRandomPassword();
        userModel.setPassword(passwordEncoder.encode(temporaryPassword));
        userRepository.save(userModel);
        Notificator notificator = notificatorFactory.get(Channels.EMAIL);
        NotificationDto notificationDto = new NotificationDto();
        notificationDto.setTo(dtp.getEmail());
        notificationDto.setVariables(
                Map.of(
                        "name", person.getFirstName() + " " + person.getLastName(),
                        "email", userModel.getEmail(),
                        "temporaryPassword", temporaryPassword,
                        "loginUrl", "https://app.viddefe.com/login"
                )
        );
        notificationDto.setTemplate("emails/invitation-email.html");
        notificationDto.setCreatedAt(Instant.now());
        notificator.send(notificationDto);
    }

    /**
     * Generates a random password for the invited user.
     * @return A randomly generated password. with at least 12 characters. 1 uppercase, 1 lowercase, 1 number, 1 special character.
     *
     */
    private String generateRandomPassword() {
        SecureRandom random = new SecureRandom();
        StringBuilder password = new StringBuilder();

        password.append(UPPER.charAt(random.nextInt(UPPER.length())));
        password.append(LOWER.charAt(random.nextInt(LOWER.length())));
        password.append(DIGITS.charAt(random.nextInt(DIGITS.length())));
        password.append(SPECIAL.charAt(random.nextInt(SPECIAL.length())));

        for (int i = 4; i < 12; i++) {
            password.append(ALL.charAt(random.nextInt(ALL.length())));
        }

        return shuffle(password.toString(), random);
    }

    private String shuffle(String input, SecureRandom random) {
        List<Character> chars = new java.util.ArrayList<>(input.chars().mapToObj(c -> (char) c).toList());
        Collections.shuffle(chars, random);
        return chars.stream().collect(StringBuilder::new, StringBuilder::append, StringBuilder::append).toString();
    }

    private List<UserPermissions> getUserPermissionsCollection(UserModel userModel, List<PermissionModel> permissionModels) {
        return permissionModels.stream().map(permissionModel -> {
            UserPermissions userPermissions = new UserPermissions();
            userPermissions.setUserModel(userModel);
            userPermissions.setPermissionModel(permissionModel);
            return userPermissions;
        }).toList();
    }
}
