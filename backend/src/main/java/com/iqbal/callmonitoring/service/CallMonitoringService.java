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

@Slf4j
@Service
@RequiredArgsConstructor
public class CallMonitoringService {

    private final CallMonitoringRepository callMonitoringRepository;
    private final DateRangeValidator dateRangeValidator;

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
                request.getSortBy(),
                request.getSortDirectionOrDefault("DESC")
        );

        // 4. Map entity ke response DTO
        List<CallMonitoringResponse> content = pagingResult.data().stream()
                .map(this::mapToResponse)
                .toList();

        // 5. Kembalikan response terpadu dengan pagination metadata terenkapsulasi
        return WebResponse.success(content, PaginationMeta.of(page, limit, pagingResult.totalRecords()));
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
