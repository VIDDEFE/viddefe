package com.viddefe.viddefe_api.churches.domain.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.viddefe.viddefe_api.StatesCities.domain.model.CitiesModel;
import com.viddefe.viddefe_api.churches.infrastructure.dto.ChurchDTO;
import com.viddefe.viddefe_api.churches.infrastructure.dto.ChurchResDto;
import com.viddefe.viddefe_api.people.domain.model.PeopleModel;
import com.viddefe.viddefe_api.people.infrastructure.dto.PeopleResDto;
import jakarta.persistence.*;
import jakarta.validation.constraints.Digits;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.*;

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
    private String phone;
    private Date foundationDate;
    @Column(precision = 10, scale = 6)
    private BigDecimal latitude;

    @Column(precision = 10, scale = 6)
    private BigDecimal longitude;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cities_id")
    private CitiesModel city;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_church_id")
    @JsonBackReference
    private ChurchModel parentChurch;

    @OneToMany(
            mappedBy = "parentChurch",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @JsonManagedReference
    private Collection<ChurchModel> children = new ArrayList<>();

    public void addChildChurch(ChurchModel childChurch) {
        children.add(childChurch);
        childChurch.setParentChurch(this);
    }

    public void removeChildChurch(ChurchModel childChurch) {
        children.remove(childChurch);
        childChurch.setParentChurch(null);
    }

    public  ChurchModel fromDto(ChurchDTO dto) {
        this.setName(dto.getName());
        this.setLongitude(dto.getLongitude());
        this.setLatitude(dto.getLatitude());
        this.setPhone(dto.getPhone());
        this.setEmail(dto.getEmail());
        this.setFoundationDate(dto.getFoundationDate());
        return this;
    }

    public ChurchResDto toDto() {
        ChurchResDto dto = new ChurchResDto(
                id,
                name,
                longitude,
                latitude,
                city.getStates().toDto(),
                city.toDto(),
                null
        );

        dto.setLongitude(longitude);
        dto.setLatitude(latitude);

        return dto;
    }
}
