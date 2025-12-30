package com.viddefe.viddefe_api.auth.domain.seeder;

import com.viddefe.viddefe_api.auth.domain.model.RolUserModel;
import com.viddefe.viddefe_api.auth.domain.repository.RolUserRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RolUserSeeder {
    private final RolUserRepository rolUserRepository;

    @PostConstruct
    public void seed() {
        if (rolUserRepository.count() == 0) {
            RolUserModel admin = new RolUserModel();
            admin.setName("ADMIN");
            rolUserRepository.save(admin);

            RolUserModel user = new RolUserModel();
            user.setName("USER");
            rolUserRepository.save(user);
        } else {
            System.out.println("Rol users already exist - seeder");
        }
    }
}
