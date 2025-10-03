package com.viddefe.viddefe_api.catalogs.Repository;

import com.viddefe.viddefe_api.catalogs.Model.CitiesModel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CitiesRepository extends JpaRepository<CitiesModel, Long> {
}
