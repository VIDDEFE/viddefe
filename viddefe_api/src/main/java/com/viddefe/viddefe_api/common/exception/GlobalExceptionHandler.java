package com.viddefe.viddefe_api.common.exception;

import com.viddefe.viddefe_api.common.response.ApiResponse;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.jpa.JpaObjectRetrievalFailureException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.sql.SQLException;
import java.time.Instant;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    private static final String TRACE_KEY = "traceId";

    // =====================================================
    //  DOMAIN / APPLICATION
    // =====================================================

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ApiResponse<Object>> handleEntityNotFound(
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

    // =====================================================
    //  VALIDATION
    // =====================================================

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Object>> handleValidation(
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

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<Object>> handleIllegalArgument(
            IllegalArgumentException ex,
            HttpServletRequest req
    ) {
        return buildResponse(
                HttpStatus.BAD_REQUEST,
                ex.getMessage(),
                "INVALID_ARGUMENT",
                req,
                null
        );
    }


    // =====================================================
    //  PERSISTENCE / DATABASE
    // =====================================================

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiResponse<Object>> handleDataIntegrity(
            DataIntegrityViolationException ex,
            HttpServletRequest req
    ) {
        log.warn("Data integrity violation", ex);

        String message = "Operation violates database constraints";


        Throwable cause = ex.getMostSpecificCause();
        if (cause instanceof SQLException psqlEx) {
            if ("23505".equals(psqlEx.getSQLState())) {
                message = "Ups algo error en nuestra base de datos, contacta con soporte.";
            }
        }

        return buildResponse(
                HttpStatus.CONFLICT,
                message,
                "DATA_INTEGRITY_VIOLATION",
                req,
                null
        );
    }


    @ExceptionHandler(JpaObjectRetrievalFailureException.class)
    public ResponseEntity<ApiResponse<Object>> handleJpaRetrieval(
            JpaObjectRetrievalFailureException ex,
            HttpServletRequest req
    ) {
        log.warn("JPA retrieval failure", ex);
        return buildResponse(
                HttpStatus.NOT_FOUND,
                "Related resource not found",
                "JPA_RETRIEVAL_FAILURE",
                req,
                null
        );
    }

    @ExceptionHandler(TransactionSystemException.class)
    public ResponseEntity<ApiResponse<Object>> handleTransaction(
            TransactionSystemException ex,
            HttpServletRequest req
    ) {
        log.error("Transaction error", ex);
        return buildResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Transaction failed",
                "TRANSACTION_ERROR",
                req,
                null
        );
    }

    // =====================================================
    //  INFRA / FRAMEWORK
    // =====================================================

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ApiResponse<Object>> handleNoHandler(
            NoHandlerFoundException ex,
            HttpServletRequest req
    ) {
        return buildResponse(
                HttpStatus.NOT_FOUND,
                "Endpoint not found",
                "ENDPOINT_NOT_FOUND",
                req,
                null
        );
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ApiResponse<Object>> handleMethodNotAllowed(
            HttpRequestMethodNotSupportedException ex,
            HttpServletRequest req
    ) {
        return buildResponse(
                HttpStatus.METHOD_NOT_ALLOWED,
                "HTTP method not allowed for this endpoint",
                "METHOD_NOT_ALLOWED",
                req,
                null
        );
    }

    // =====================================================
    //  404 - Static resource not found (Spring 6)
    // =====================================================
        @ExceptionHandler(NoResourceFoundException.class)
        public ResponseEntity<ApiResponse<Object>> handleNoResourceFound(
                NoResourceFoundException ex,
                HttpServletRequest req
        ) {
            return buildResponse(
                    HttpStatus.NOT_FOUND,
                    "Endpoint not found",
                    "ENDPOINT_NOT_FOUND",
                    req,
                    null
            );
        }


    // =====================================================
    //  FALLBACK 500
    // =====================================================

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Object>> handleUnhandled(
            Exception ex,
            HttpServletRequest req
    ) {
        log.error("Unhandled exception", ex);
        return buildResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Internal server error",
                "INTERNAL_ERROR",
                req,
                null
        );
    }

    // =====================================================
    //  RESPONSE BUILDER (CENTRALIZED)
    // =====================================================

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
            if (auth != null && auth.isAuthenticated()) {
                meta.put("user", auth.getName());
            }
        } catch (Exception ignored) {}

        if (extraMeta != null) meta.putAll(extraMeta);

        response.withMeta(Collections.unmodifiableMap(meta));

        return ResponseEntity.status(status).body(response);
    }
}
