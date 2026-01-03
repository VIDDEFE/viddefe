package com.viddefe.viddefe_api.finances.domain.model;

import com.viddefe.viddefe_api.finances.infrastructure.dto.CreateOfferingDto;
import com.viddefe.viddefe_api.finances.infrastructure.dto.OfferingDto;
import com.viddefe.viddefe_api.people.domain.model.PeopleModel;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Table(name = "offerings")
@Entity
@Getter @Setter
public class Offerings {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private UUID eventId;

    @Column(nullable = false)
    private Double amount;

    @ManyToOne
    @JoinColumn(name = "offering_type_id", nullable = false)
    private OfferingType offeringType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "people_id", nullable = false)
    private PeopleModel person;

    public OfferingDto toDto() {
        OfferingDto dto = new OfferingDto();
        dto.setEventId(this.eventId);
        dto.setAmount(this.amount);
        dto.setPeople(this.person.toDto());
        dto.setType(this.offeringType.toDto());
        return dto;
    }

    public Offerings fromDto(CreateOfferingDto dto){
        this.eventId = dto.getEventId();
        this.amount = dto.getAmount();
        return this;
    }
}
