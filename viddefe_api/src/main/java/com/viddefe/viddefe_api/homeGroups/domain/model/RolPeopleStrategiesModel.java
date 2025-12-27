package com.viddefe.viddefe_api.homeGroups.domain.model;

import com.viddefe.viddefe_api.people.domain.model.PeopleModel;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Table(
        name = "rol_people_strategies",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_role_person_strategy",
                        columnNames = {"role_id", "person_id","strategy_id"}
                )
        }

)
@Entity
@Getter @Setter
public class RolPeopleStrategiesModel {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "role_id", nullable = false)
    private RolesStrategiesModel role;

    @ManyToOne
    @JoinColumn(name = "person_id", nullable = false)
    private PeopleModel person;
}
