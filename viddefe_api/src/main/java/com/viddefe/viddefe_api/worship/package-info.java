@ApplicationModule(
    id = "worship-module",
    displayName = "Worship Module",
    allowedDependencies = {"people", "church_module", "common", "common :: response-api", "common :: jwt", "church_module :: models", "church_module :: services", "auth-module :: service", "auth-module :: dto"}
)
package com.viddefe.viddefe_api.worship;

import org.springframework.modulith.ApplicationModule;