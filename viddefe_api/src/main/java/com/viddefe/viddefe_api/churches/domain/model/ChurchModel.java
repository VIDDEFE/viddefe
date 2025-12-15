package com.viddefe.viddefe_api.churches.domain.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.viddefe.viddefe_api.StatesCities.domain.model.CitiesModel;
import com.viddefe.viddefe_api.churches.infrastructure.dto.ChurchDTO;
import com.viddefe.viddefe_api.churches.infrastructure.dto.ChurchResDto;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;
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
    private String email;
    private Long phone;
    private Date foundationDate;
    private BigDecimal latitude;
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
        model.setLatitude(dto.getLatitude());
        model.setPhone(dto.getPhone());
        model.setEmail(dto.getEmail());
        model.setFoundationDate(dto.getFoundationDate());
        return model;
    }

    public ChurchResDto toDto() {
        {
            return new ChurchResDto(
                    this.id,
                    this.name,
                    this.longitude,
                    this.latitude,
                    this.city.getState().toDto(),
                    this.city.toDto()
            );
        }
    }
}