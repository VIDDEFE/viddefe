package com.viddefe.viddefe_api.common.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class ApiResponse<T> {

    private boolean success;            // true / false
    private int status;                 // HTTP status code (200, 400...)
    private String message;             // human-friendly message
    private String errorCode;           // optional internal code
    private T data;                     // payload
    private Map<String, Object> metadata;   // extra metadata
    private Instant timestamp;          // ISO-8601 server timestamp

    public static <T> ApiResponse<T> ok(T data) {
        return ApiResponse.<T>builder()
                .success(true).status(200).message("OK").data(data).timestamp(Instant.now()).build();
    }

    public static <T> ApiResponse<T> created(T data) {
        return ApiResponse.<T>builder()
                .success(true).status(201).message("Created").data(data).timestamp(Instant.now()).build();
    }

    public static <T> ApiResponse<T> noContent() {
        return ApiResponse.<T>builder()
                .success(true).status(204).message("No Content").timestamp(Instant.now()).build();
    }

    public static <T> ApiResponse<T> error(int status, String message, String errorCode) {
        return ApiResponse.<T>builder()
                .success(false).status(status).message(message).errorCode(errorCode).timestamp(Instant.now()).build();
    }

    public ApiResponse<T> withMeta(Map<String, Object> meta) {
        this.metadata = meta;
        return this;
    }
}
