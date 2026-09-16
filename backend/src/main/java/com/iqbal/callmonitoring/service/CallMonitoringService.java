package com.iqbal.callmonitoring.service;

import com.iqbal.callmonitoring.dto.request.CallMonitoringFilterRequest;
import com.iqbal.callmonitoring.dto.response.CallMonitoringResponse;
import com.iqbal.callmonitoring.dto.response.PaginationMeta;
import com.iqbal.callmonitoring.dto.response.WebResponse;
import com.iqbal.callmonitoring.entity.CallMonitoring;
import com.iqbal.callmonitoring.repository.CallMonitoringRepository;
import com.iqbal.callmonitoring.repository.base.PagingResult;
import com.iqbal.callmonitoring.validator.DateRangeValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class CallMonitoringService {

    private final CallMonitoringRepository callMonitoringRepository;
    private final DateRangeValidator dateRangeValidator;

    private static final Map<String, String> ALLOWED_SORT_COLUMNS = Map.of(
            "call_id", "cm.call_id",
            "callid", "cm.call_id",
            "call_timestamp", "cm.call_timestamp",
            "calltimestamp", "cm.call_timestamp",
            "cs_name", "cm.cs_name",
            "csname", "cm.cs_name",
            "customer_name", "cm.customer_name",
            "customername", "cm.customer_name",
            "sentiment_score", "cm.sentiment_score",
            "sentimentscore", "cm.sentiment_score"
    );

    public WebResponse<List<CallMonitoringResponse>> getCallMonitorings(CallMonitoringFilterRequest request) {
        log.info("Processing call monitorings query with custom repository: {}", request);

        // 1. Validate period rules (max 3 months)
        dateRangeValidator.validate(request.getStartDate(), request.getEndDate());

        // 2. Resolve pagination parameters
        int page = (request.getPage() != null && request.getPage() > 0) ? request.getPage() : 1;
        int limit = (request.getLimit() != null && request.getLimit() > 0) ? request.getLimit() : 5;

        // 3. Resolve sorting column & direction safely
        String requestedSortBy = (request.getSortBy() != null) ? request.getSortBy().toLowerCase() : "call_timestamp";
        String sortColumn = ALLOWED_SORT_COLUMNS.getOrDefault(requestedSortBy, "cm.call_timestamp");
        String sortDirection = "asc".equalsIgnoreCase(request.getSortOrder()) ? "ASC" : "DESC";

        // 4. Single unified call for paging data and total count
        PagingResult<CallMonitoring> pagingResult = callMonitoringRepository.findWithPaging(
                request, page, limit, sortColumn, sortDirection
        );

        long totalRecords = pagingResult.totalRecords();
        List<CallMonitoringResponse> content = pagingResult.data().stream()
                .map(this::mapToResponse)
                .toList();

        // 5. Calculate pagination metadata
        int totalPages = (int) Math.ceil((double) totalRecords / limit);
        boolean hasPrevious = page > 1;
        boolean hasNext = page < totalPages;

        PaginationMeta meta = PaginationMeta.builder()
                .page(page)
                .limit(limit)
                .totalRecords(totalRecords)
                .totalPages(totalPages)
                .hasPrevious(hasPrevious)
                .hasNext(hasNext)
                .build();

        return WebResponse.success(content, meta);
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
