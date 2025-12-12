@ApplicationModule(
        displayName = "ChurchModule",
        id = "church_module",
        allowedDependencies = {
            "StatesCities :: states_cities_model","StatesCities :: services",
            "common :: response-api"
        }
)

package com.viddefe.viddefe_api.churches;

import org.springframework.modulith.ApplicationModule;