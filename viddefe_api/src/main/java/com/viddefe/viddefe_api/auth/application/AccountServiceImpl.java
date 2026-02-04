package com.viddefe.viddefe_api.auth.application;

import com.viddefe.viddefe_api.auth.Infrastructure.dto.InvitationDto;
import com.viddefe.viddefe_api.auth.contracts.AccountService;
import com.viddefe.viddefe_api.auth.contracts.PermissionService;
import com.viddefe.viddefe_api.auth.domain.model.PermissionModel;
import com.viddefe.viddefe_api.auth.domain.model.RolUserModel;
import com.viddefe.viddefe_api.auth.domain.model.UserModel;
import com.viddefe.viddefe_api.auth.domain.model.UserPermissions;
import com.viddefe.viddefe_api.auth.domain.repository.UserRepository;
import com.viddefe.viddefe_api.notifications.Infrastructure.dto.NotificationAccountEvent;
import com.viddefe.viddefe_api.notifications.Infrastructure.dto.NotificationEvent;
import com.viddefe.viddefe_api.notifications.common.Channels;
import com.viddefe.viddefe_api.config.rabbit.RabbitPriority;
import com.viddefe.viddefe_api.notifications.contracts.NotificationEventPublisher;
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
    private final RolesUserService rolesUserService;
    private final PermissionService permissionService;
    private final NotificationEventPublisher notificationEventPublisher;

    /**
     * Temporary one-time password.
     * - Generated randomly
     * - Valid for a short period
     * - Must be changed on first login
     * - Never reused
     */
    private static final String TEMPLATE_INVITATION_MESSAGE = """
    Hello {{name}}, welcome to VidDefe!
    
    Your account has been created.
    
    Username: {{username}}
    Temporary password (one-time): {{password}}
    
    You will be required to change this password immediately after logging in.
    """;

    @Override
    public void invite(InvitationDto dtp, UUID churchId) {
        // Implementation goes here
        if((dtp.getEmail() != null && !dtp.getEmail().isBlank()) && userRepository.existsByEmail(dtp.getEmail())) {
            throw new DataIntegrityViolationException("User with email already exists");
        }else if((dtp.getPhone() != null && !dtp.getPhone().isBlank()) && userRepository.existsByPhone(dtp.getPhone())) {
            throw new DataIntegrityViolationException("User with phone number already exists");
        }else if(userRepository.existsUserByPeopleIdAndPeopleChurchId(
                dtp.getPersonId(),
                peopleReader.getPeopleById(dtp.getPersonId()).getChurch().getId()
        )){
            throw new DataIntegrityViolationException("User for the selected person already exists in the church");
        };

        List<PermissionModel> permissionModels = permissionService.findByListNames(dtp.getPermissions());

        RolUserModel role = rolesUserService.foundRolUserById(dtp.getRole());
        PeopleModel person = peopleReader.getPeopleById(dtp.getPersonId());
        UserModel userModel = new UserModel();
        userModel.setEmail(dtp.getEmail());
        userModel.setPhone(dtp.getPhone());
        userModel.setPeople(person);
        userModel.setRolUser(role);
        List<UserPermissions> userPermissions = getUserPermissionsCollection(userModel, permissionModels);
        userModel.addListPermission(userPermissions);
        String temporaryPassword = generateRandomPassword();
        userModel.setPassword(passwordEncoder.encode(temporaryPassword));
        userRepository.save(userModel);
        Channels channel = Channels.from(dtp.getChannel());
        NotificationAccountEvent event = new NotificationAccountEvent();
        event.setPriority(RabbitPriority.HIGH);
        event.setSubject("Bienvenido a VidDefe!");
        event.setChannels(channel);
        event.setPersonId(person.getId());
        event.setCreatedAt(Instant.now());
        event.setVariables(resolveVariables(event, person, userModel, temporaryPassword));
        event.setTemplate("/emails/invitation.html");
        notificationEventPublisher.publish(event);
    }

    public Map<String, Object> resolveVariables(NotificationEvent event, PeopleModel person, UserModel user, String temporaryPassword) {

        Map<String, Object> vars = new HashMap<>();

        vars.put("name", person.getFirstName() + " " + person.getLastName());

        switch (event.getChannels()) {
            case EMAIL -> {
                vars.put("email", user.getEmail());
                vars.put("temporaryPassword", temporaryPassword);
                vars.put("loginUrl", "https://app.viddefe.com/login");
            }
            case WHATSAPP -> {
                vars.put("username", user.getPhone());
                vars.put("password", temporaryPassword);
            }
            default -> throw new IllegalStateException(
                    "Unsupported channel: " + event.getChannels()
            );
        }

        return vars;
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

    private String buildMessage(Map<String, String> variables) {
        String message = TEMPLATE_INVITATION_MESSAGE;
        for (Map.Entry<String, String> entry : variables.entrySet()) {
            message = message.replace("{{" + entry.getKey() + "}}", entry.getValue());
        }
        return message;
    }
}
