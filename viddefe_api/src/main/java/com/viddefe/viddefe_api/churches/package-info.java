@ApplicationModule(
        displayName = "ChurchModule",
        id = "church_module",
        allowedDependencies = {"StatesCities :: states_cities_model", "StatesCities :: services", "StatesCities:: dtos", "common :: response-api", "config-module :: jwt", "people :: services", "people :: people-model", "people :: dto", "common :: jwt", "auth-module :: service", "auth-module :: dto", "worship-module :: meeting-dto"}
)

package com.viddefe.viddefe_api.churches;

import org.springframework.modulith.ApplicationModule;