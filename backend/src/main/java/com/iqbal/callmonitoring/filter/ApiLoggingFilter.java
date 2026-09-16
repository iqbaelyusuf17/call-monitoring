package com.iqbal.callmonitoring.filter;

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

import java.io.IOException;
import java.util.UUID;

/**
 * HTTP Filter untuk distributed tracing dan structured observability.
 * Menghasilkan traceId unik, menyematkannya ke response header X-Trace-Id dan SLF4J MDC,
 * serta mencatat waktu eksekusi request.
 */
@Slf4j
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class ApiLoggingFilter extends OncePerRequestFilter {

    public static final String TRACE_ID_HEADER = X-Trace-Id;
    public static final String MDC_TRACE_ID_KEY = traceId;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        long startTime = System.currentTimeMillis();

        String traceId = request.getHeader(TRACE_ID_HEADER);
        if (traceId == null || traceId.isBlank()) {
            traceId = UUID.randomUUID().toString().replace(-, ");
 }

 MDC.put(MDC_TRACE_ID_KEY, traceId);
 response.setHeader(TRACE_ID_HEADER, traceId);

 String uri = request.getRequestURI();
 String queryString = request.getQueryString() != null ? ? + request.getQueryString() : ;
 String method = request.getMethod();

 log.info([HTTP IN] {} {}{}, method, uri, queryString);

 try {
 filterChain.doFilter(request, response);
 } finally {
 long duration = System.currentTimeMillis() - startTime;
 int status = response.getStatus();
 log.info([HTTP OUT] {} {}{} - Status: {} (took {} ms), method, uri, queryString, status, duration);
 MDC.remove(MDC_TRACE_ID_KEY);
 }
 }
}
