@ApplicationModule(
        id = "auth-module",
        displayName = "Authentication Module",
        allowedDependencies = {
                "people","people :: services","people :: people-model",
                "common","common :: response-api","common :: response-exception",
                "config-module :: jwt"
        }
)

package com.viddefe.viddefe_api.auth;

import org.springframework.modulith.ApplicationModule;