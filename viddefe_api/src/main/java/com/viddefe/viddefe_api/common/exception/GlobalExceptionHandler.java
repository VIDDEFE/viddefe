package com.viddefe.viddefe_api.common.exception;

import com.viddefe.viddefe_api.common.response.ApiResponse;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    private static final String TRACE_KEY = "traceId";

    // ---------------------------------------------
    // 404 Not Found
    // ---------------------------------------------
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ApiResponse<Object>> handleNotFound(
            EntityNotFoundException ex,
            HttpServletRequest req
    ) {
        return buildResponse(
                HttpStatus.NOT_FOUND,
                ex.getMessage(),
                "NOT_FOUND",
                req,
                null
        );
    }


    // ---------------------------------------------
    // 401 Unauthorized
    // ---------------------------------------------
    @ExceptionHandler(CustomExceptions.InvalidCredentialsException.class)
    public ResponseEntity<ApiResponse<Object>> handleInvalidCredentials(
            CustomExceptions.InvalidCredentialsException ex,
            HttpServletRequest req
    ) {
        return buildResponse(HttpStatus.UNAUTHORIZED, ex.getMessage(), "INVALID_CREDENTIALS", req, null);
    }

    // ---------------------------------------------
    // 400 Bad Request
    // ---------------------------------------------
    @ExceptionHandler(CustomExceptions.BadRequestException.class)
    public ResponseEntity<ApiResponse<Object>> handleBadRequest(
            CustomExceptions.BadRequestException ex,
            HttpServletRequest req
    ) {
        return buildResponse(HttpStatus.BAD_REQUEST, ex.getMessage(), "BAD_REQUEST", req, null);
    }

    // ---------------------------------------------
    // 409 Conflict (Already exists)
    // ---------------------------------------------
    @ExceptionHandler(CustomExceptions.ResourceAlreadyExistsException.class)
    public ResponseEntity<ApiResponse<Object>> handleAlreadyExists(
            CustomExceptions.ResourceAlreadyExistsException ex,
            HttpServletRequest req
    ) {
        return buildResponse(HttpStatus.CONFLICT, ex.getMessage(), "RESOURCE_ALREADY_EXISTS", req, null);
    }

    // ---------------------------------------------
    // 403 Forbidden
    // ---------------------------------------------
    @ExceptionHandler(CustomExceptions.UnauthorizedException.class)
    public ResponseEntity<ApiResponse<Object>> handleForbidden(
            CustomExceptions.UnauthorizedException ex,
            HttpServletRequest req
    ) {
        return buildResponse(HttpStatus.FORBIDDEN, ex.getMessage(), "FORBIDDEN", req, null);
    }

    // ---------------------------------------------
    // 409 Conflict genérico
    // ---------------------------------------------
    @ExceptionHandler(CustomExceptions.ConflictException.class)
    public ResponseEntity<ApiResponse<Object>> handleConflict(
            CustomExceptions.ConflictException ex,
            HttpServletRequest req
    ) {
        return buildResponse(HttpStatus.CONFLICT, ex.getMessage(), "CONFLICT", req, null);
    }

    // ---------------------------------------------
    // Validation (400) → devuelve errores normalizados
    // ---------------------------------------------
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Object>> handleValidationException(
            MethodArgumentNotValidException ex,
            HttpServletRequest req
    ) {

        Map<String, Object> fieldErrors = new HashMap<>();

        ex.getBindingResult().getFieldErrors().forEach(err ->
                fieldErrors.put(err.getField(), err.getDefaultMessage())
        );

        Map<String, Object> meta = Map.of("fields", fieldErrors);

        return buildResponse(
                HttpStatus.BAD_REQUEST,
                "Validation failed",
                "VALIDATION_ERROR",
                req,
                meta
        );
    }

    // ---------------------------------------------
    // Catch-all 500
    // ---------------------------------------------
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Object>> handleGeneralException(Exception ex, HttpServletRequest req) {
        log.error("Unhandled exception", ex);
        return buildResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Internal server error",
                "INTERNAL_ERROR",
                req,
                null
        );
    }

    // =============================================
    //  CENTRALIZADOR
    // =============================================
    private ResponseEntity<ApiResponse<Object>> buildResponse(
            HttpStatus status,
            String message,
            String errorCode,
            HttpServletRequest req,
            Map<String, Object> extraMeta
    ) {

        ApiResponse<Object> response = ApiResponse.error(
                status.value(),
                message,
                errorCode
        );

        Map<String, Object> meta = new HashMap<>();
        meta.put("timestamp", Instant.now().toString());
        meta.put("path", req.getRequestURI());

        String trace = MDC.get(TRACE_KEY);
        if (trace != null) meta.put("traceId", trace);

        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.isAuthenticated() && auth.getPrincipal() != null) {
                meta.put("user", auth.getName());
            }
        } catch (Exception ignored) {}

        if (extraMeta != null) meta.putAll(extraMeta);

        response.withMeta(Collections.unmodifiableMap(meta));

        return ResponseEntity.status(status).body(response);
    }
}
