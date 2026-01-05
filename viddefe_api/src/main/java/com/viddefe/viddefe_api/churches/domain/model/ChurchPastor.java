package com.viddefe.viddefe_api.churches.domain.model;

import com.viddefe.viddefe_api.people.domain.model.PeopleModel;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "church_pastors")
@Getter
@Setter
public class ChurchPastor {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "pastor_id", nullable = false)
    private PeopleModel pastor;

    @ManyToOne(optional = false)
    @JoinColumn(name = "church_id", nullable = false)
    private ChurchModel church;
}
