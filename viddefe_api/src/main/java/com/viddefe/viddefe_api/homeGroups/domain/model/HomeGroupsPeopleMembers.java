package com.viddefe.viddefe_api.homeGroups.domain.model;

import com.viddefe.viddefe_api.homeGroups.domain.model.serializable.HomeGroupPeopleMembersId;
import com.viddefe.viddefe_api.people.domain.model.PeopleModel;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "home_groups_people_members")
@Getter @Setter
@Builder
@AllArgsConstructor @NoArgsConstructor
public class HomeGroupsPeopleMembers {
    @EmbeddedId
    private HomeGroupPeopleMembersId homeGroupPeopleMembersId;

    @MapsId("homeGroupId")
    @ManyToOne
    @JoinColumn(name = "home_group_id", nullable = false)
    private HomeGroupsModel homeGroup;

    @MapsId("peopleId")
    @ManyToOne
    @JoinColumn(name = "people_id", nullable = false)
    private PeopleModel people;
}
