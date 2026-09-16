package com.iqbal.callmonitoring.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class ApiLoggingFilter extends OncePerRequestFilter {

    public static final String TRACE_ID_HEADER = "X-Trace-Id";
    public static final String MDC_TRACE_ID_KEY = "traceId";

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        // 1. Resolve or generate Trace ID
        String traceId = request.getHeader(TRACE_ID_HEADER);
        if (traceId == null || traceId.isBlank()) {
            traceId = UUID.randomUUID().toString();
        }

        MDC.put(MDC_TRACE_ID_KEY, traceId);
        response.setHeader(TRACE_ID_HEADER, traceId);

        // 2. Wrap request & response to safely cache body payload
        ContentCachingRequestWrapper requestWrapper = new ContentCachingRequestWrapper(request);
        ContentCachingResponseWrapper responseWrapper = new ContentCachingResponseWrapper(response);

        long startTime = System.currentTimeMillis();

        // 3. Extract request metadata & headers
        String method = request.getMethod();
        String uri = request.getRequestURI();
        String queryString = request.getQueryString() != null ? "?" + request.getQueryString() : "";
        Map<String, String> headers = extractHeaders(request);

        log.info("[HTTP REQUEST] traceId=[{}] | {} {}{} | Headers: {}",
                traceId, method, uri, queryString, headers);

        try {
            filterChain.doFilter(requestWrapper, responseWrapper);
        } finally {
            long duration = System.currentTimeMillis() - startTime;
            int status = responseWrapper.getStatus();

            // 4. Extract Request Body (after controller read/bind)
            String requestBody = getPayloadFromBytes(requestWrapper.getContentAsByteArray());
            if (!requestBody.isBlank()) {
                log.info("[HTTP REQUEST BODY] traceId=[{}] | Payload: {}", traceId, requestBody);
            }

            // 5. Extract Response Body
            String responseBody = getPayloadFromBytes(responseWrapper.getContentAsByteArray());
            log.info("[HTTP RESPONSE] traceId=[{}] | Status: {} | Duration: {}ms | Response Payload: {}",
                    traceId, status, duration, responseBody);

            // 6. IMPORTANT: Copy cached body back to actual response stream
            responseWrapper.copyBodyToResponse();
            MDC.remove(MDC_TRACE_ID_KEY);
        }
    }

    private Map<String, String> extractHeaders(HttpServletRequest request) {
        Map<String, String> headerMap = new HashMap<>();
        Enumeration<String> headerNames = request.getHeaderNames();
        if (headerNames != null) {
            while (headerNames.hasMoreElements()) {
                String name = headerNames.nextElement();
                String value = request.getHeader(name);
                // Mask sensitive headers if present
                if ("authorization".equalsIgnoreCase(name) || "cookie".equalsIgnoreCase(name)) {
                    value = "******";
                }
                headerMap.put(name, value);
            }
        }
        return headerMap;
    }

    private String getPayloadFromBytes(byte[] content) {
        if (content == null || content.length == 0) {
            return "-";
        }
        return new String(content, StandardCharsets.UTF_8).replaceAll("\\s+", " ").trim();
    }
}
