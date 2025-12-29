package com.viddefe.viddefe_api.homeGroups.application;

import com.viddefe.viddefe_api.homeGroups.contracts.RolesPeopleStrategiesService;
import com.viddefe.viddefe_api.homeGroups.contracts.RolesStrategiesReader;
import com.viddefe.viddefe_api.homeGroups.domain.model.RolPeopleStrategiesModel;
import com.viddefe.viddefe_api.homeGroups.domain.model.RolesStrategiesModel;
import com.viddefe.viddefe_api.homeGroups.domain.repository.RolesPeopleStrategiesRepository;
import com.viddefe.viddefe_api.homeGroups.infrastructure.dto.AssignPeopleToRoleDto;
import com.viddefe.viddefe_api.people.contracts.PeopleReader;
import com.viddefe.viddefe_api.people.domain.model.PeopleModel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RolesPeopleStrategesServiceImpl implements RolesPeopleStrategiesService {
    private final RolesPeopleStrategiesRepository rolesPeopleStrategiesRepository;
    private final PeopleReader peopleReader;
    private final RolesStrategiesReader rolesStrategiesReader;
    @Override
    public void assignRoleToPeopleInStrategy(UUID roleId, AssignPeopleToRoleDto peopleIds) {
        List<PeopleModel> listOfPeople = peopleReader.getPeopleByIds(peopleIds.getPeopleIds());

        RolesStrategiesModel rolesStrategiesModel = rolesStrategiesReader.getRoleStrategyById(roleId);
        List<RolPeopleStrategiesModel> rolesPeopleStrategiesModels = listOfPeople.stream().map(people -> {
            RolPeopleStrategiesModel rolPeopleStrategiesModel = new RolPeopleStrategiesModel();
            rolPeopleStrategiesModel.setRole(rolesStrategiesModel);
            rolPeopleStrategiesModel.setPerson(people);
            return rolPeopleStrategiesModel;
        }).toList();
        rolesPeopleStrategiesRepository.saveAll(rolesPeopleStrategiesModels);
    }

    @Override
    public void removeRoleFromPeopleInStrategy(UUID roleId, AssignPeopleToRoleDto peopleId) {
        List<PeopleModel> listOfPeople = peopleReader.getPeopleByIds(peopleId.getPeopleIds());

        RolesStrategiesModel rolesStrategiesModel = rolesStrategiesReader.getRoleStrategyById(roleId);
        List<RolPeopleStrategiesModel> rolesPeopleStrategiesModels = listOfPeople.stream().map(people -> {
            RolPeopleStrategiesModel rolPeopleStrategiesModel = new RolPeopleStrategiesModel();
            rolPeopleStrategiesModel.setRole(rolesStrategiesModel);
            rolPeopleStrategiesModel.setPerson(people);
            return rolPeopleStrategiesModel;
        }).toList();
        rolesPeopleStrategiesRepository.deleteAll(rolesPeopleStrategiesModels);
    }
}
