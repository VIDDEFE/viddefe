package com.viddefe.viddefe_api.people.model;

import com.viddefe.viddefe_api.StatesCities.Model.StatesModel;
import com.viddefe.viddefe_api.churches.ChurchModel;
import com.viddefe.viddefe_api.people.dto.PeopleDTO;
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
    private UUID id; // varchar(40) en YAML

    @Column(length = 40, nullable = false)
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

    @ManyToOne
    @JoinColumn(name = "church_id")
    private ChurchModel church;

    public PeopleModel fromDto(PeopleDTO dto){
        this.firstName = dto.getFirstName();
        this.lastName = dto.getLastName();
        this.cc = dto.getCc();
        this.phone = dto.getPhone();
        this.birthdate = dto.getBirthdate();
        return this;
    }

}
