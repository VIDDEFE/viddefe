package com.viddefe.viddefe_api.people.domain.model;

import com.viddefe.viddefe_api.people.infrastructure.dto.PeopleTypeDto;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "types_people")
@Getter
@Setter
public class PeopleTypeModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;

    public PeopleTypeDto toDto() {
        return new PeopleTypeDto(id, name);
    }
}
