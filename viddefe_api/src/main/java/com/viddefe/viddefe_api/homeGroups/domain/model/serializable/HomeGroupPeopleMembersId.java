package com.viddefe.viddefe_api.homeGroups.domain.model.serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;
import java.util.UUID;

@Embeddable
@EqualsAndHashCode
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class HomeGroupPeopleMembersId implements Serializable {
    @Column(name = "home_group_id")
    private UUID homeGroupId;

    @Column(name = "people_id")
    private UUID peopleId;
}
