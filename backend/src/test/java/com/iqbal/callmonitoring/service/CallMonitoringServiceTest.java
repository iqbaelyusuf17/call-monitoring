package com.iqbal.callmonitoring.service;

import com.iqbal.callmonitoring.dto.request.CallMonitoringFilterRequest;
import com.iqbal.callmonitoring.dto.response.CallMonitoringResponse;
import com.iqbal.callmonitoring.dto.response.WebResponse;
import com.iqbal.callmonitoring.entity.CallMonitoring;
import com.iqbal.callmonitoring.exception.BadRequestException;
import com.iqbal.callmonitoring.repository.CallMonitoringRepository;
import com.iqbal.callmonitoring.repository.base.PagingResult;
import com.iqbal.callmonitoring.validator.DateRangeValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CallMonitoringServiceTest {

    @Mock
    private CallMonitoringRepository callMonitoringRepository;

    @Spy
    private DateRangeValidator dateRangeValidator = new DateRangeValidator();

    @InjectMocks
    private CallMonitoringService callMonitoringService;

    private List<CallMonitoring> sampleEntities;

    @BeforeEach
    void setUp() {
        sampleEntities = List.of(
                CallMonitoring.builder()
                        .callId("CALL-001")
                        .callTimestamp(OffsetDateTime.now())
                        .csName("Andi")
                        .customerName("Budi")
                        .sentimentScore(new BigDecimal("85.50"))
                        .build(),
                CallMonitoring.builder()
                        .callId("CALL-002")
                        .callTimestamp(OffsetDateTime.now())
                        .csName("Siti")
                        .customerName("Dewi")
                        .sentimentScore(new BigDecimal("62.00"))
                        .build()
        );
    }

    @Test
    @DisplayName("Should return paginated call monitoring list successfully")
    void shouldReturnPaginatedCallMonitorings() {
        when(callMonitoringRepository.findWithPaging(any(), eq(1), eq(5), anyString(), anyString()))
                .thenReturn(new PagingResult<>(sampleEntities, 2L));

        CallMonitoringFilterRequest request = CallMonitoringFilterRequest.builder()
                .page(1)
                .limit(5)
                .build();

        WebResponse<List<CallMonitoringResponse>> response = callMonitoringService.getCallMonitorings(request);

        assertThat(response.getCode()).isEqualTo(200);
        assertThat(response.getData()).hasSize(2);
        assertThat(response.getMeta().getPage()).isEqualTo(1);
        assertThat(response.getMeta().getLimit()).isEqualTo(5);
        assertThat(response.getMeta().getTotalRecords()).isEqualTo(2);
        assertThat(response.getMeta().getTotalPages()).isEqualTo(1);
        assertThat(response.getMeta().isHasPrevious()).isFalse();
        assertThat(response.getMeta().isHasNext()).isFalse();

        verify(dateRangeValidator).validate(null, null);
    }

    @Test
    @DisplayName("Should throw BadRequestException when start date is after end date")
    void shouldThrowWhenStartDateIsAfterEndDate() {
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = startDate.minusDays(5);

        CallMonitoringFilterRequest request = CallMonitoringFilterRequest.builder()
                .startDate(startDate)
                .endDate(endDate)
                .build();

        assertThatThrownBy(() -> callMonitoringService.getCallMonitorings(request))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Start period cannot be after end period");
    }

    @Test
    @DisplayName("Should throw BadRequestException when period exceeds 3 months")
    void shouldThrowWhenPeriodExceedsThreeMonths() {
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusMonths(4);

        CallMonitoringFilterRequest request = CallMonitoringFilterRequest.builder()
                .startDate(startDate)
                .endDate(endDate)
                .build();

        assertThatThrownBy(() -> callMonitoringService.getCallMonitorings(request))
                .isInstanceOf(BadRequestException.class);
    }

    @Test
    @DisplayName("Should return empty list when no records match filter (Empty State)")
    void shouldReturnEmptyListWhenNoRecordsMatch() {
        when(callMonitoringRepository.findWithPaging(any(), eq(1), eq(5), anyString(), anyString()))
                .thenReturn(new PagingResult<>(Collections.emptyList(), 0L));

        CallMonitoringFilterRequest request = CallMonitoringFilterRequest.builder()
                .search("NonExistentKeyword")
                .page(1)
                .limit(5)
                .build();

        WebResponse<List<CallMonitoringResponse>> response = callMonitoringService.getCallMonitorings(request);

        assertThat(response.getCode()).isEqualTo(200);
        assertThat(response.getData()).isEmpty();
        assertThat(response.getMeta().getTotalRecords()).isEqualTo(0);
        assertThat(response.getMeta().getTotalPages()).isEqualTo(0);
    }
}
