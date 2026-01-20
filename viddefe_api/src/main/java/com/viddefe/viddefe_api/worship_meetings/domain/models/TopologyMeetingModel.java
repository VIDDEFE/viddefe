package com.viddefe.viddefe_api.worship_meetings.domain.models;

import com.viddefe.viddefe_api.worship_meetings.configuration.TopologyEventType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "topology_meetings")
@Getter @Setter
public class TopologyMeetingModel {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String name;

    @OneToMany(mappedBy = "topologyMeeting", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MeetingType> meetingTypes;

    @Enumerated(EnumType.STRING)
    private TopologyEventType type;
}
