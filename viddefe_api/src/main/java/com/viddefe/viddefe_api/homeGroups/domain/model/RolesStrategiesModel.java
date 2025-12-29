package com.viddefe.viddefe_api.homeGroups.domain.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.viddefe.viddefe_api.homeGroups.infrastructure.dto.CreateRolesStrategiesDto;
import com.viddefe.viddefe_api.homeGroups.infrastructure.dto.RolesStrategiesDto;
import com.viddefe.viddefe_api.homeGroups.infrastructure.dto.base.AbstractRoleTreeDto;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Table(
        name = "roles_strategies",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_role_name_strategy",
                        columnNames = {"name", "strategy_id"}
                )
        }
)
@Entity
@Getter @Setter
public class RolesStrategiesModel {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String name;

    @ManyToOne
    @JoinColumn(name = "strategy_id", nullable = false)
    private StrategiesModel strategy;

    @ManyToOne
    @JoinColumn(name = "parent_role_id")
    private RolesStrategiesModel parentRole;

    @OneToMany(mappedBy = "parentRole")
    @JsonBackReference
    private Set<RolesStrategiesModel> children = new HashSet<>();

    @OneToMany(mappedBy = "role", fetch = FetchType.LAZY)
    @JsonBackReference
    private Set<RolPeopleStrategiesModel> rolPeople = new HashSet<>();

    public AbstractRoleTreeDto toDto(){
        AbstractRoleTreeDto dto = new RolesStrategiesDto();
        dto.setName(this.name);
        List<AbstractRoleTreeDto> childrenDto = children.stream()
                .map(RolesStrategiesModel::toDto)
                .collect(Collectors.toList());
        dto.setChildren(childrenDto);
        return dto;
    }

    public RolesStrategiesModel fromDto(CreateRolesStrategiesDto dto){
        this.name = dto.getName();
        return this;
    }

}
