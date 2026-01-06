@ApplicationModule(
    id = "worship-module",
    displayName = "Worship Module",
    allowedDependencies = {"people", "church_module", "common", "common :: response-api", "common :: jwt", "church_module :: models", "church_module :: services", "auth-module :: service", "auth-module :: dto", "people :: people-model", "people :: dto", "people :: services", "homeGroups", "homeGroups :: models", "homeGroups :: services"}
)
package com.viddefe.viddefe_api.worship_meetings;

import org.springframework.modulith.ApplicationModule;