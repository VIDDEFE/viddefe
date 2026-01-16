package com.viddefe.viddefe_api.notifications.common;

import java.util.Map;

public class ResolverMessage {
    public static String resolveMessage(String template, Map<String, Object> variables) {
        String message = template;
        for (java.util.Map.Entry<String, Object> entry : variables.entrySet()) {
            String placeholder = "{{" + entry.getKey() + "}}";
            message = message.replace(placeholder, entry.getValue().toString());
        }
        return message;
    }
}
