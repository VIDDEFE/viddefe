package com.viddefe.viddefe_api.homeGroups.contracts;

import com.viddefe.viddefe_api.homeGroups.infrastructure.dto.CreateHomeGroupsDto;
import com.viddefe.viddefe_api.homeGroups.infrastructure.dto.HomeGroupsDTO;
import com.viddefe.viddefe_api.homeGroups.infrastructure.dto.HomeGroupsDetailDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface HomeGroupService {
    /**
     * Create a new home group.
     * @param dto {@link CreateHomeGroupsDto} containing the details of the home group to be created
     * returns the created HomeGroupsDTO {@link HomeGroupsDTO}
     */
    HomeGroupsDTO createHomeGroup(CreateHomeGroupsDto dto, UUID churchId);

    /**
     * Update an existing home group.
     * @param dto {@link CreateHomeGroupsDto} containing the updated details of the home group
     * @param id  UUID of the home group to be updated
     * @return
     */
    HomeGroupsDTO updateHomeGroup(CreateHomeGroupsDto dto, UUID id);

    /**
     * Get a home group by its ID.
     * @param id UUID of the home group to be retrieved
     * @return HomeGroupsDTO {@link HomeGroupsDTO} of the retrieved home group
     */
    HomeGroupsDetailDto getHomeGroupById(UUID id);

    /**
     * Get a paginated list of home groups for a specific church.
     * @param pageable {@link Pageable} containing pagination information
     * @param churchId UUID of the church whose home groups are to be retrieved
     * @return Page<HomeGroupsDTO> {@link HomeGroupsDTO} containing the paginated list of home groups
     */
    Page<HomeGroupsDTO> getHomeGroups(Pageable pageable, UUID churchId);

    /**
     * Delete a home group by its ID.
     * @param id UUID of the home group to be deleted
     */
    Void deleteHomeGroup(UUID id);

}

