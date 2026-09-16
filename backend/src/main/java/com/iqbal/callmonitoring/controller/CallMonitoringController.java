package com.iqbal.callmonitoring.controller;

import com.iqbal.callmonitoring.dto.request.CallMonitoringFilterRequest;
import com.iqbal.callmonitoring.dto.response.CallMonitoringResponse;
import com.iqbal.callmonitoring.dto.response.WebResponse;
import com.iqbal.callmonitoring.service.CallMonitoringService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(/api/v1)
@RequiredArgsConstructor
public class CallMonitoringController {

    private final CallMonitoringService service;

    /**
     * Endpoint: GET /api/v1/call-monitoring
     * Menerima query parameter: search, startDate, endDate, sentiment, page, limit, sortBy, sortOrder
     */
    @GetMapping(/call-monitoring)
    public ResponseEntity<WebResponse<List<CallMonitoringResponse>>> getCallMonitorings(
            @ModelAttribute CallMonitoringFilterRequest request
    ) {
        log.info(Received GET /api/v1/call-monitoring with filter: {}, request);
        WebResponse<List<CallMonitoringResponse>> response = service.getCallMonitorings(request);
        return ResponseEntity.ok(response);
    }
}
