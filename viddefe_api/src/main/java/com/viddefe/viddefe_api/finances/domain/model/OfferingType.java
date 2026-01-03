package com.viddefe.viddefe_api.finances.domain.model;

import com.viddefe.viddefe_api.finances.infrastructure.dto.OfferingTypeDto;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "offering_types")
@Getter @Setter
public class OfferingType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    public OfferingTypeDto toDto() {
        OfferingTypeDto dto = new OfferingTypeDto();
        dto.setId(this.id);
        dto.setName(this.name);
        return dto;
    }
}
