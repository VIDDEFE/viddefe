package com.viddefe.viddefe_api.churches.domain.model;

import com.viddefe.viddefe_api.people.domain.model.PeopleModel;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Table(name = "church_pastors")
@Entity
@Getter @Setter
public class ChurchPastor {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "pastor_id", referencedColumnName = "id")
    private PeopleModel pastor;

    @ManyToOne
    @JoinColumn(name = "church_id", referencedColumnName = "id")
    private ChurchModel church;
}
