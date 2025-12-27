package com.viddefe.viddefe_api.homeGroups.domain.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

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
}
