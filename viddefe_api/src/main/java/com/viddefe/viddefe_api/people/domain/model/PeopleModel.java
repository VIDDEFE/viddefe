package com.viddefe.viddefe_api.people.domain.model;

import com.viddefe.viddefe_api.StatesCities.domain.model.StatesModel;
import com.viddefe.viddefe_api.churches.domain.model.ChurchModel;
import com.viddefe.viddefe_api.people.infrastructure.dto.PeopleDTO;
import com.viddefe.viddefe_api.people.infrastructure.dto.PeopleResDto;
import com.viddefe.viddefe_api.people.infrastructure.dto.PeopleTypeDto;
import com.viddefe.viddefe_api.StatesCities.infrastructure.dto.StatesDto;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(
        name = "people",
        uniqueConstraints = {
                @UniqueConstraint(name = "uq_people_cc_church", columnNames = {"cc", "church_id"})
        }
)
@Getter
@Setter
public class PeopleModel {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(length = 40, nullable = false)
    private String cc;

    @Column(name = "first_name", length = 256, nullable = false)
    private String firstName;

    @Column(name = "last_name", length = 256, nullable = false)
    private String lastName;

    @Column(length = 256, nullable = false)
    private String phone;

    private LocalDate birthDate;

    @ManyToOne
    @JoinColumn(name = "type_person_id")
    private PeopleTypeModel typePerson;

    @ManyToOne
    @JoinColumn(name = "state_id")
    private StatesModel state;

    @Column(length = 256)
    private String avatar;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "church_id")
    private ChurchModel church;

    public PeopleModel fromDto(PeopleDTO dto){
        this.firstName = dto.getFirstName();
        this.lastName = dto.getLastName();
        this.cc = dto.getCc();
        this.phone = dto.getPhone();
        this.birthDate = dto.getBirthDate();
        return this;
    }

    public PeopleResDto toDto(){
        // Convertir birthDate de forma segura
        java.util.Date birth = null;
        if (this.birthDate != null) {
            birth = java.sql.Date.valueOf(this.birthDate);
        }

        // Convertir typePerson y state de forma segura (pueden ser null en tests)
        PeopleTypeDto typeDto = null;
        if (this.typePerson != null) {
            typeDto = this.typePerson.toDto();
        }

        StatesDto stateDto = null;
        if (this.state != null) {
            stateDto = this.state.toDto();
        }

        return new PeopleResDto(
                this.id,
                this.cc,
                this.firstName,
                this.lastName,
                this.phone,
                this.avatar,
                birth,
                typeDto,
                stateDto,
                null


        );
    }

}
