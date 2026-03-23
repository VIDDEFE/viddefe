package com.viddefe.viddefe_api.homegroups.domain.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.viddefe.viddefe_api.homegroups.domain.model.HomeGroupsPeopleMembers;
import com.viddefe.viddefe_api.homegroups.domain.model.serializable.HomeGroupPeopleMembersId;
import com.viddefe.viddefe_api.people.domain.model.PeopleModel;

public interface HomeGroupMembersRepository extends JpaRepository<HomeGroupsPeopleMembers, HomeGroupPeopleMembersId> {
    @Query("""
        SELECT p
        FROM PeopleModel p
        JOIN FETCH p.state
        JOIN FETCH p.typePerson 
        JOIN HomeGroupsPeopleMembers hpm ON hpm.people.id = p.id
        WHERE hpm.homeGroupPeopleMembersId.homeGroupId = :homeGroupId
    """)
    Page<PeopleModel> findMembersByHomeGroupId(UUID homeGroupId, Pageable pageable);
    @Query("""
        SELECT hpm.homeGroupPeopleMembersId.peopleId
        FROM HomeGroupsPeopleMembers hpm
        WHERE hpm.homeGroupPeopleMembersId.homeGroupId = :homeGroupId
    """)
    List<UUID> findMemberIdsByHomeGroupId(UUID homeGroupId);
}
