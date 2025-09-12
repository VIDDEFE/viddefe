package com.viddefe.viddefe_api.auth.seeder;

import com.viddefe.viddefe_api.auth.Config.RolesUser;
import com.viddefe.viddefe_api.auth.model.RolUserModel;
import com.viddefe.viddefe_api.auth.repository.RolUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
public class RolUserSeeder implements CommandLineRunner {
    private final RolUserRepository rolUserRepository;
    @Override
    public void run(String... args) throws Exception {
        if(!rolUserRepository.findAll().isEmpty()) {
            System.out.println("Rol users already exist - seeder");
            return;
        }
        List<RolUserModel> rolUserModels = Arrays.stream(RolesUser.values()).map( item -> {
            RolUserModel rolUserModel = new RolUserModel();
            rolUserModel.setName(item.name());
            return rolUserModel;
        }).toList();
        rolUserRepository.saveAll(rolUserModels);
        System.out.println("Rol users created - seeder");
    }
}
