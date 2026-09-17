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
        log.info("Processing call monitorings query: {}", request);

        // 1. Validasi periode waktu (maks 3 bulan)
        dateRangeValidator.validate(request.getStartDate(), request.getEndDate());

        // 2. Resolve parameter paging & sorting
        int page = request.getPageOrDefault(1);
        int limit = request.getLimitOrDefault(5);

        // 3. Panggil repository dengan query terpadu (data & total count)
        PagingResult<CallMonitoring> pagingResult = callMonitoringRepository.findWithPaging(
                request,
                page,
                limit,
                resolveSortColumn(request.getSortBy()),
                request.getSortDirectionOrDefault("DESC")
        );

        // 4. Map entity ke response DTO
        List<CallMonitoringResponse> content = pagingResult.data().stream()
                .map(this::mapToResponse)
                .toList();

        // 5. Kembalikan response terpadu dengan pagination metadata terenkapsulasi
        return WebResponse.success(content, PaginationMeta.of(page, limit, pagingResult.totalRecords()));
    }

    private String resolveSortColumn(String sortBy) {
        if (sortBy == null) {
            return "cm.call_timestamp";
        }
        return ALLOWED_SORT_COLUMNS.getOrDefault(sortBy.toLowerCase(), "cm.call_timestamp");
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
