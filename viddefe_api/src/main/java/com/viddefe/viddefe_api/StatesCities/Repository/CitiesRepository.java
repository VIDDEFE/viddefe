package com.viddefe.viddefe_api.StatesCities.Repository;

import com.viddefe.viddefe_api.StatesCities.Model.CitiesModel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CitiesRepository extends JpaRepository<CitiesModel, Long> {
}
