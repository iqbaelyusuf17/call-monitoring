package com.iqbal.callmonitoring.service;

import com.iqbal.callmonitoring.dto.request.CallMonitoringFilterRequest;
import com.iqbal.callmonitoring.dto.response.CallMonitoringResponse;
import com.iqbal.callmonitoring.dto.response.PaginationMeta;
import com.iqbal.callmonitoring.dto.response.WebResponse;
import com.iqbal.callmonitoring.entity.CallMonitoring;
import com.iqbal.callmonitoring.repository.CallMonitoringRepository;
import com.iqbal.callmonitoring.repository.base.PagingResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * Service Layer untuk orkestrasi pemrosesan data monitoring panggilan.
 * Mengikuti best practice modern Spring Boot: direct @Service class.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CallMonitoringService {

    private final CallMonitoringRepository repository;

    private static final Map<String, String> SORT_COLUMN_MAP = Map.of(
            call_id, cm.call_id,
            callid, cm.call_id,
            call_timestamp, cm.call_timestamp,
            calltimestamp, cm.call_timestamp,
            cs_name, cm.cs_name,
            csname, cm.cs_name,
            customer_name, cm.customer_name,
            customername, cm.customer_name,
            sentiment_score, cm.sentiment_score,
            sentimentscore, cm.sentiment_score
    );

    public WebResponse<List<CallMonitoringResponse>> getCallMonitorings(CallMonitoringFilterRequest request) {
        if (request == null) {
            request = new CallMonitoringFilterRequest();
        }

        int page = Math.max(1, request.getPage());
        int limit = Math.max(1, Math.min(100, request.getLimit() <= 0 ? 10 : request.getLimit()));

        String requestedSortBy = request.getSortBy() != null ? request.getSortBy().toLowerCase() : call_timestamp;
        String sortColumn = SORT_COLUMN_MAP.getOrDefault(requestedSortBy, cm.call_timestamp);
        String sortDirection = asc.equalsIgnoreCase(request.getSortOrder()) ? ASC : DESC;

        log.info(Fetching call monitorings - Page: {}, Limit: {}, Sort: {} {}, page, limit, sortColumn, sortDirection);

        PagingResult<CallMonitoring> result = repository.findWithPaging(request, page, limit, sortColumn, sortDirection);

        List<CallMonitoringResponse> dtoList = result.data().stream()
                .map(this::mapToResponse)
                .toList();

        PaginationMeta meta = PaginationMeta.builder()
                .page(result.page())
                .limit(result.limit())
                .totalRecords(result.totalRecords())
                .totalPages(result.totalPages())
                .hasPrevious(result.page() > 1)
                .hasNext(result.page() < result.totalPages())
                .build();

        return WebResponse.success(dtoList, meta);
    }

    private CallMonitoringResponse mapToResponse(CallMonitoring entity) {
        return CallMonitoringResponse.builder()
                .callId(entity.getCallId())
                .callTimestamp(entity.getCallTimestamp())
                .csName(entity.getCsName())
                .customerName(entity.getCustomerName())
                .sentimentScore(entity.getSentimentScore())
                .build();
    }
}
