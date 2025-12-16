@ApplicationModule(
        id = "auth-module",
        displayName = "Authentication Module",
        allowedDependencies = {"people", "people :: services", "people :: people-model", "common", "common :: response-api", "common :: response-exception", "config-module :: jwt", "church_module :: models", "church_module", "church_module :: dto", "people :: dto", "church_module :: services"}
)

package com.viddefe.viddefe_api.auth;

import org.springframework.modulith.ApplicationModule;