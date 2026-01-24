@ApplicationModule(
        id = "people",
        displayName = "People",
        allowedDependencies = {"StatesCities", "StatesCities :: states_cities_model", "StatesCities :: services", "church_module", "church_module :: services", "church_module :: models", "common :: response-api", "common :: response-exception", "StatesCities :: dtos", "auth-module", "auth-module :: service", "auth-module :: dto", "common :: jwt", "worship-module :: attendance_config", "worship-module :: meeting_models", "worship-module :: meeting-dto"}
)

package com.viddefe.viddefe_api.people;

import org.springframework.modulith.ApplicationModule;

