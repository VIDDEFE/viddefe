package com.viddefe.viddefe_api.homeGroups.domain.repository;

import com.viddefe.viddefe_api.homeGroups.domain.model.HomeGroupsModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface HomeGroupsRepository extends JpaRepository<HomeGroupsModel, UUID> {
    Page<HomeGroupsModel> findAllByChurchId(UUID churchId, Pageable pageable);
}
