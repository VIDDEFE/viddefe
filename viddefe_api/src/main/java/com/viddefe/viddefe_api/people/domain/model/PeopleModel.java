package com.viddefe.viddefe_api.people.domain.model;

import com.viddefe.viddefe_api.StatesCities.domain.model.StatesModel;
import com.viddefe.viddefe_api.churches.domain.model.ChurchModel;
import com.viddefe.viddefe_api.people.infrastructure.dto.PeopleDTO;
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

    @Column(length = 40, nullable = false, unique = true)
    private String cc;

    @Column(name = "first_name", length = 256, nullable = false)
    private String firstName;

    @Column(name = "last_name", length = 256, nullable = false)
    private String lastName;

    @Column(length = 256, nullable = false)
    private String phone;

    private LocalDate birthdate;

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
        this.birthdate = dto.getBirthDate();
        return this;
    }

    public PeopleDTO toDto(){
        PeopleDTO dto = new PeopleDTO();
        dto.setId(this.id);
        dto.setFirstName(this.firstName);
        dto.setLastName(this.lastName);
        dto.setCc(this.cc);
        dto.setPhone(this.phone);
        dto.setBirthDate(this.birthdate);
        dto.setTypePersonId(this.typePerson != null ? this.typePerson.getId() : null);
        dto.setStateId(this.state != null ? this.state.getId() : null);
        dto.setAvatar(this.avatar);
        dto.setChurchId(this.church != null ? this.church.getId() : null);
        return dto;
    }

}
