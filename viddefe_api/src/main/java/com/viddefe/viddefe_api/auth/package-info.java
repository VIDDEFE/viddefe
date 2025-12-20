@ApplicationModule(
        id = "auth-module",
        displayName = "Authentication Module",
        allowedDependencies = {"people", "people :: services", "people :: people-model", "common", "common :: response-api", "common :: response-exception", "config-module :: jwt", "church_module :: models", "church_module", "church_module :: dto", "people :: dto", "church_module :: services", "common :: jwt", "notifications", "notifications-module :: service", "notifications-module", "notifications-module :: factory", "notifications-module :: config", "notifications-module :: dto"}
)

package com.viddefe.viddefe_api.auth;

import org.springframework.modulith.ApplicationModule;