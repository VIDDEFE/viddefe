package com.viddefe.viddefe_api.homeGroups.domain.model;

import com.viddefe.viddefe_api.churches.domain.model.ChurchModel;
import com.viddefe.viddefe_api.homeGroups.infrastructure.dto.StrategyDto;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Table(
        name = "strategies",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_strategy_name", columnNames = {"name","church_id"})
        }
)
@Entity
@Getter @Setter
public class StrategiesModel {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private String name;

    @ManyToOne
    @JoinColumn(name = "church_id", nullable = false)
    private ChurchModel church;

    public StrategiesModel fromDto(StrategyDto dto){
        this.name = dto.getName();
        return this;
    }

    public StrategyDto toDto(){
        StrategyDto dto = new StrategyDto();
        dto.setId(this.id);
        dto.setName(this.name);
        return dto;
    }
}
