package com.viddefe.viddefe_api.worship.domain.models;

import com.viddefe.viddefe_api.churches.domain.model.ChurchModel;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.UUID;

@Table(name = "worship_services")
@Entity
@Getter @Setter
public class WorshipModel {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String name;
    private String description;
    private Date creationDate;
    private Date scheduledDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "worship_meeting_type_id", nullable = false)
    private WorshipMeetingTypes worshipType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "church_id", nullable = false)
    private ChurchModel church;
}
