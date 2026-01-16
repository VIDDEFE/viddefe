package com.viddefe.viddefe_api.homeGroups.domain.model;

import com.viddefe.viddefe_api.churches.domain.model.ChurchModel;
import com.viddefe.viddefe_api.homeGroups.infrastructure.dto.CreateHomeGroupsDto;
import com.viddefe.viddefe_api.homeGroups.infrastructure.dto.HomeGroupsDTO;
import com.viddefe.viddefe_api.people.domain.model.PeopleModel;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Table(name = "home_groups")
@Entity
@Getter @Setter
public class HomeGroupsModel {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "name", nullable = false)
    private String name;
    private String description;

    @Column(
            name = "latitude",
            nullable = false,
            precision = 10,
            scale = 8
    )
    private BigDecimal latitude;

    @Column(
            name = "longitude",
            nullable = false,
            precision = 11,
            scale = 8
    )
    private BigDecimal longitude;


    @ManyToOne
    @JoinColumn(name = "strategy_id", nullable = false)
    private StrategiesModel strategy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "leader_id", nullable = false)
    private PeopleModel manager;

    @ManyToOne
    @JoinColumn(name = "church_id", nullable = false)
    private ChurchModel church;

    public HomeGroupsModel fromDto(CreateHomeGroupsDto dto){
        this.name = dto.getName();
        this.description = dto.getDescription();
        this.latitude = dto.getLatitude();
        this.longitude = dto.getLongitude();
        return this;
    }

    public HomeGroupsDTO toDto(){
        HomeGroupsDTO dto = new HomeGroupsDTO();
        dto.setId(this.id);
        dto.setName(this.name);
        dto.setDescription(this.description);
        dto.setLatitude(this.latitude);
        dto.setLongitude(this.longitude);
        dto.setManager(this.manager.toDto());
        dto.setStrategy(this.strategy.toDto());
        return dto;
    }

}
