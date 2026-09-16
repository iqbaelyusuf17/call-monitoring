package com.iqbal.callmonitoring.controller;

import com.iqbal.callmonitoring.dto.request.CallMonitoringFilterRequest;
import com.iqbal.callmonitoring.dto.response.CallMonitoringResponse;
import com.iqbal.callmonitoring.dto.response.PaginationMeta;
import com.iqbal.callmonitoring.dto.response.WebResponse;
import com.iqbal.callmonitoring.exception.BadRequestException;
import com.iqbal.callmonitoring.exception.GlobalExceptionHandler;
import com.iqbal.callmonitoring.service.CallMonitoringService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.iqbal.callmonitoring.config.ApiLoggingFilter;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;

@WebMvcTest(controllers = CallMonitoringController.class)
@Import({GlobalExceptionHandler.class, ApiLoggingFilter.class})
class CallMonitoringControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CallMonitoringService callMonitoringService;

    @Test
    @DisplayName("GET /api/v1/call-monitoring should return 200 with data")
    void shouldReturnCallMonitoringsSuccessfully() throws Exception {
        CallMonitoringResponse item = CallMonitoringResponse.builder()
                .callId("CALL-20260901-001")
                .callTimestamp(OffsetDateTime.parse("2026-09-01T08:30:00+07:00"))
                .csName("Andi Pratama")
                .customerName("Budi Santoso")
                .sentimentScore(new BigDecimal("85.50"))
                .build();

        PaginationMeta meta = PaginationMeta.builder()
                .page(1)
                .limit(5)
                .totalRecords(1)
                .totalPages(1)
                .hasPrevious(false)
                .hasNext(false)
                .build();

        when(callMonitoringService.getCallMonitorings(any(CallMonitoringFilterRequest.class)))
                .thenReturn(WebResponse.success(List.of(item), meta));

        mockMvc.perform(get("/api/v1/call-monitoring")
                        .param("page", "1")
                        .param("limit", "5")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(header().exists("X-Trace-Id"))
                .andExpect(jsonPath("$.code", is(200)))
                .andExpect(jsonPath("$.message", is("Success")))
                .andExpect(jsonPath("$.data", hasSize(1)))
                .andExpect(jsonPath("$.data[0].call_id", is("CALL-20260901-001")))
                .andExpect(jsonPath("$.data[0].call_timestamp", notNullValue()))
                .andExpect(jsonPath("$.data[0].cs_name", is("Andi Pratama")))
                .andExpect(jsonPath("$.data[0].customer_name", is("Budi Santoso")))
                .andExpect(jsonPath("$.data[0].sentiment_score", is(85.50)))
                .andExpect(jsonPath("$.meta.total_records", is(1)))
                .andExpect(jsonPath("$.meta.has_previous", is(false)))
                .andExpect(jsonPath("$.meta.has_next", is(false)));
    }

    @Test
    @DisplayName("GET /api/v1/call-monitoring with invalid period should return 400 Bad Request")
    void shouldReturnBadRequestWhenServiceThrowsBadRequestException() throws Exception {
        when(callMonitoringService.getCallMonitorings(any(CallMonitoringFilterRequest.class)))
                .thenThrow(new BadRequestException("Period range cannot exceed 3 months"));

        mockMvc.perform(get("/api/v1/call-monitoring")
                        .param("startDate", "2026-01-01")
                        .param("endDate", "2026-06-01")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(header().exists("X-Trace-Id"))
                .andExpect(jsonPath("$.code", is(400)))
                .andExpect(jsonPath("$.message", is("Period range cannot exceed 3 months")));
    }

    @Test
    @DisplayName("GET /api/v1/call-monitoring should bind snake_case query params to camelCase fields in request DTO")
    void shouldBindSnakeCaseQueryParamsToCamelCaseFields() throws Exception {
        org.mockito.ArgumentCaptor<CallMonitoringFilterRequest> captor =
                org.mockito.ArgumentCaptor.forClass(CallMonitoringFilterRequest.class);

        PaginationMeta meta = PaginationMeta.builder().page(1).limit(5).totalRecords(0).totalPages(0).build();
        when(callMonitoringService.getCallMonitorings(captor.capture()))
                .thenReturn(WebResponse.success(List.of(), meta));

        mockMvc.perform(get("/api/v1/call-monitoring")
                        .param("start_date", "2026-07-01")
                        .param("end_date", "2026-08-31")
                        .param("sort_by", "sentiment_score")
                        .param("sort_order", "asc")
                        .param("search", "Budi")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        CallMonitoringFilterRequest captured = captor.getValue();
        org.junit.jupiter.api.Assertions.assertEquals(java.time.LocalDate.of(2026, 7, 1), captured.getStartDate());
        org.junit.jupiter.api.Assertions.assertEquals(java.time.LocalDate.of(2026, 8, 31), captured.getEndDate());
        org.junit.jupiter.api.Assertions.assertEquals("sentiment_score", captured.getSortBy());
        org.junit.jupiter.api.Assertions.assertEquals("asc", captured.getSortOrder());
        org.junit.jupiter.api.Assertions.assertEquals("Budi", captured.getSearch());
    }
}
