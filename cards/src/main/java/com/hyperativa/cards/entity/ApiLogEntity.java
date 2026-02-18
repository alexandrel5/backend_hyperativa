package com.hyperativa.cards.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Entity representing an audit log entry for API requests/responses.
 * Tracks who did what, when, how long it took, and optionally sanitized request/response bodies.
 */
@Entity
@Table(name = "api_logs",
        indexes = {
                @Index(name = "idx_owner_action", columnList = "owner_sub, action, created_at"),
                @Index(name = "idx_correlation", columnList = "correlation_id")
        })
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"requestBody", "responseBody"}) // avoid logging huge bodies
public class ApiLogEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;  // changed to Long/BIGINT for future scale

    /**
     * Keycloak user identifier (sub claim)
     * Nullable for anonymous / unauthenticated requests
     */
    @Column(name = "owner_sub", columnDefinition = "BINARY(16)")
    private UUID ownerSub;

    /**
     * Correlation ID for distributed tracing (useful when you have multiple services)
     */
    @Column(name = "correlation_id", length = 36)
    private String correlationId;

    @Column(nullable = false, length = 10)
    private String method;

    @Column(nullable = false, length = 500)
    private String path;

    @Column(length = 1000)
    private String queryString;

    @Column(nullable = false)
    private Integer status;

    @Column(nullable = false)
    private Integer durationMs;

    @Column(length = 45)
    private String ipAddress;

    /**
     * High-level action name (e.g. "CREATE_SINGLE_CARD", "UPLOAD_BATCH", "FETCH_CARD")
     * Easier to query / report than raw path
     */
    @Column(nullable = false, length = 150)
    private String action;

    /**
     * Sanitized / truncated request body (JSON usually)
     * Should be masked (no full PAN, tokens, passwords)
     */
    @Column(columnDefinition = "TEXT")
    private String requestBody;

    /**
     * Sanitized / truncated response body
     * Usually only logged on error (4xx/5xx) or for small responses
     */
    @Column(columnDefinition = "TEXT")
    private String responseBody;

    /**
     * Exception message / stack trace summary (only on error)
     */
    @Column(columnDefinition = "TEXT")
    private String errorMessage;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    // Optional: pre-persist hook to ensure createdAt is always set
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}

