package com.hyperativa.cards.filter;

import com.hyperativa.cards.entity.ApiLogEntity;
import com.hyperativa.cards.service.impl.ApiLogServiceImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 10)
public class ApiAuditLoggingFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(ApiAuditLoggingFilter.class);

    private final ApiLogServiceImpl apiLogService;

    private static final String API_PREFIX = "/api/";

    public ApiAuditLoggingFilter(ApiLogServiceImpl apiLogService) {
        this.apiLogService = apiLogService;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getRequestURI();

        if (!path.startsWith(API_PREFIX)) {
            return true;
        }
        return false;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        long start = System.currentTimeMillis();
        ContentCachingResponseWrapper responseWrapper = new ContentCachingResponseWrapper(response);

        String correlationId = UUID.randomUUID().toString();
        MDC.put("correlationId", correlationId);

        try {
            filterChain.doFilter(request, responseWrapper);

            // ──── NOW the security context SHOULD be populated ────
            UUID ownerSub = getCurrentUserSub();           // capture here, synchronously
            String requestBody  = safeGetRequestBody(request);
            String responseBody = safeGetResponseBody(responseWrapper);

            // Pass captured values to async task
            logApiCallAsync(request, responseWrapper, start, correlationId, null,
                    ownerSub, requestBody, responseBody);

        } catch (Exception ex) {
            // You can capture ownerSub here too if you want to log failed requests
            UUID ownerSub = getCurrentUserSub(); // may still be null on early auth failure
            logApiCallAsync(request, responseWrapper, start, correlationId, ex,
                    ownerSub, null, null);
            throw ex;

        } finally {
            responseWrapper.copyBodyToResponse();
            MDC.clear();
        }
    }

    private void logApiCallAsync(
            HttpServletRequest req,
            ContentCachingResponseWrapper res,
            long start,
            String correlationId,
            Exception ex,
            UUID ownerSub,             // ← passed in
            String requestBody,
            String responseBody) {

        CompletableFuture.runAsync(() -> {
            try {
                ApiLogEntity log = new ApiLogEntity();
                log.setOwnerSub(ownerSub);   // already captured — no lookup needed
                log.setCorrelationId(correlationId);
                log.setMethod(req.getMethod());
                log.setPath(req.getRequestURI());
                log.setQueryString(req.getQueryString());
                log.setStatus(res.getStatus());
                log.setDurationMs((int) (System.currentTimeMillis() - start));
                log.setIpAddress(req.getRemoteAddr());
                log.setAction(determineAction(req));
                log.setRequestBody(maskSensitiveData(requestBody));
                log.setResponseBody(maskSensitiveData(responseBody));
                log.setErrorMessage(ex != null ? ex.getMessage() : null);

                apiLogService.saveLog(log);

            } catch (Exception e) {
                log.error("Failed to persist audit log", e);
            }
        });
    }

    // Helper methods
    private UUID getCurrentUserSub() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        log.info("Authentication present? {}", auth != null);
        if (auth != null) {
            log.info("Principal type: {}", auth.getPrincipal().getClass().getName());
        }
        return null;
    }

    private String safeGetRequestBody(HttpServletRequest req) {
        // Implement with ContentCachingRequestWrapper if needed
        // For simplicity - skip or use another wrapper
        return null; // ← enhance later
    }

    private String safeGetResponseBody(ContentCachingResponseWrapper res) {
        byte[] buf = res.getContentAsByteArray();
        if (buf.length == 0) return null;
        try {
            return new String(buf, res.getCharacterEncoding());
        } catch (UnsupportedEncodingException e) {
            return "[encoding error]";
        }
    }

    private String maskSensitiveData(String body) {
        if (body == null) return null;
        // Use regex or Jackson to mask card numbers, tokens, etc.
        return body.replaceAll("\\d{13,19}", "****MASKED_CARD****");
    }

    private String determineAction(HttpServletRequest req) {
        // Simple mapping – improve with @RequestMapping inspection if needed
        String path = req.getRequestURI();
        if (path.contains("/create")) return "CREATE_CARD";
        if (path.contains("/upload")) return "UPLOAD_BATCH";
        return req.getMethod() + " " + path;
    }
}