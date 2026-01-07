package com.viddefe.viddefe_api.worship_meetings.domain.models;

import com.viddefe.viddefe_api.worship_meetings.infrastructure.dto.MinistryFunctionTypeDto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "ministry_function_types")
@Getter @Setter
@AllArgsConstructor @NoArgsConstructor
public class MinistryFunctionTypes {
    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    public MinistryFunctionTypeDto toDto() {
        MinistryFunctionTypeDto dto = new MinistryFunctionTypeDto();
        dto.setId(this.id);
        dto.setName(this.name);
        return dto;
    }
}
