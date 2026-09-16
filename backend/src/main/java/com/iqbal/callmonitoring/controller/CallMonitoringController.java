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
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class CallMonitoringController {

    private final CallMonitoringService callMonitoringService;

    @GetMapping("/call-monitoring")
    public ResponseEntity<WebResponse<List<CallMonitoringResponse>>> getCallMonitorings(
            @ModelAttribute CallMonitoringFilterRequest request
    ) {
        log.info("Received request to get call monitorings with params: {}", request);
        WebResponse<List<CallMonitoringResponse>> response = callMonitoringService.getCallMonitorings(request);
        return ResponseEntity.ok(response);
    }
}
