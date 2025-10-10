package com.viddefe.viddefe_api.churches;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.viddefe.viddefe_api.StatesCities.Model.CitiesModel;
import com.viddefe.viddefe_api.StatesCities.Model.StatesModel;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "churches")
@Getter
@Setter
public class ChurchModel {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String name;
    private Long latitude;
    private BigDecimal longitude;
    @ManyToOne
    @JoinColumn(name = "cities_id")
    private CitiesModel city;
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "parent_church_id")
    @JsonBackReference
    private ChurchModel parentChurch;

    public static ChurchModel fromDto(ChurchDTO dto) {
        ChurchModel model = new ChurchModel();
        if (dto.getId() != null) {
            model.setId(dto.getId());
        }
        model.setName(dto.getName());
        model.setLongitude(dto.getLongitude());
        return model;
    }
}