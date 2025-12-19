package com.viddefe.viddefe_api.auth.application;

import com.viddefe.viddefe_api.auth.Infrastructure.dto.PermissionSeedRequest;
import com.viddefe.viddefe_api.auth.contracts.PermissionEnum;
import com.viddefe.viddefe_api.auth.contracts.PermissionService;
import com.viddefe.viddefe_api.auth.domain.model.PermissionModel;
import com.viddefe.viddefe_api.auth.domain.repository.PermissionRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PermissionServiceImpl implements PermissionService {
    private final PermissionRepository permissionRepository;

    @Override
    public List<PermissionModel> findAll() {
        return permissionRepository.findAll();
    }


    @Override
    public void seed(PermissionSeedRequest request) {
        for (PermissionEnum permission : request.permissions()) {

            boolean exists = permissionRepository.existsByName(permission.getName());
            if (exists) continue;
            System.out.println("Seeding permission: " + permission.getName() + " from source: " + request.source());
            PermissionModel model = new PermissionModel();
            model.setName(permission.getName());
            permissionRepository.save(model);
        }
    }

    @Override
    public PermissionModel findByName(String name) {
        return permissionRepository.findByName(name).orElseThrow(
                () -> new EntityNotFoundException("Permission not found: " + name
        ));
    }

    @Override
    public List<PermissionModel> findByListNames(List<String> names) {
        return permissionRepository.findAllByNameIn(names);
    }
}
