package com.viddefe.viddefe_api.StatesCities.domain.model;

import com.viddefe.viddefe_api.StatesCities.infrastructure.dto.CitiesDto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "cities")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CitiesModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @ManyToOne
    @JoinColumn(name = "state_id")
    private StatesModel state;

    public CitiesModel(String name, StatesModel state) {
        this.name = name;
        this.state = state;
    }

    public CitiesDto toDto(){
        return new CitiesDto(this.id, this.name);
    }
}
