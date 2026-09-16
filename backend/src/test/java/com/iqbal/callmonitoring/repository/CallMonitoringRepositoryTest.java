package com.iqbal.callmonitoring.repository;

import com.iqbal.callmonitoring.dto.request.CallMonitoringFilterRequest;
import com.iqbal.callmonitoring.entity.CallMonitoring;
import com.iqbal.callmonitoring.repository.base.PagingResult;
import com.iqbal.callmonitoring.repository.impl.CallMonitoringRepositoryImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@Import(CallMonitoringRepositoryImpl.class)
class CallMonitoringRepositoryTest {

    @Autowired
    private CallMonitoringRepository repository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        jdbcTemplate.execute("DELETE FROM call_monitorings");

        String insertSql = "INSERT INTO call_monitorings (call_id, call_timestamp, cs_name, customer_name, sentiment_score) " +
                "VALUES (?, ?, ?, ?, ?)";

        jdbcTemplate.update(insertSql,
                "CALL-20260701-001",
                Timestamp.from(OffsetDateTime.of(2026, 7, 1, 10, 0, 0, 0, ZoneOffset.UTC).toInstant()),
                "Andi Pratama", "Budi Santoso", new BigDecimal("85.50"));

        jdbcTemplate.update(insertSql,
                "CALL-20260715-002",
                Timestamp.from(OffsetDateTime.of(2026, 7, 15, 11, 30, 0, 0, ZoneOffset.UTC).toInstant()),
                "Siti Nurhaliza", "Dewi Anggraini", new BigDecimal("62.00"));

        jdbcTemplate.update(insertSql,
                "CALL-20260801-003",
                Timestamp.from(OffsetDateTime.of(2026, 8, 1, 14, 0, 0, 0, ZoneOffset.UTC).toInstant()),
                "Rian Hidayat", "Ahmad Fauzi", new BigDecimal("70.00"));

        jdbcTemplate.update(insertSql,
                "CALL-20260820-004",
                Timestamp.from(OffsetDateTime.of(2026, 8, 20, 15, 45, 0, 0, ZoneOffset.UTC).toInstant()),
                "Maya Putri", "Siti Rahmawati", new BigDecimal("45.00"));
    }

    @Test
    @DisplayName("AC-7: Should filter by sentiment UNDER_70 (< 70.00)")
    void shouldFilterUnder70Sentiment() {
        CallMonitoringFilterRequest request = CallMonitoringFilterRequest.builder()
                .sentiment("UNDER_70")
                .build();

        PagingResult<CallMonitoring> result =
                repository.findWithPaging(request, 1, 5, "cm.call_timestamp", "DESC");

        assertThat(result.totalRecords()).isEqualTo(2);
        assertThat(result.data()).hasSize(2);
        assertThat(result.data()).allMatch(item -> item.getSentimentScore().compareTo(new BigDecimal("70.00")) < 0);
    }

    @Test
    @DisplayName("AC-8: Should filter by sentiment 70_AND_ABOVE (>= 70.00)")
    void shouldFilter70AndAboveSentiment() {
        CallMonitoringFilterRequest request = CallMonitoringFilterRequest.builder()
                .sentiment("70_AND_ABOVE")
                .build();

        PagingResult<CallMonitoring> result =
                repository.findWithPaging(request, 1, 5, "cm.call_timestamp", "DESC");

        assertThat(result.totalRecords()).isEqualTo(2);
        assertThat(result.data()).hasSize(2);
        assertThat(result.data()).allMatch(item -> item.getSentimentScore().compareTo(new BigDecimal("70.00")) >= 0);
    }

    @Test
    @DisplayName("AC-4: Should search keyword across all columns")
    void shouldSearchKeywordAcrossColumns() {
        // Search CS Name
        CallMonitoringFilterRequest req1 = CallMonitoringFilterRequest.builder().search("Maya").build();
        var res1 = repository.findWithPaging(req1, 1, 5, "cm.call_timestamp", "DESC");
        assertThat(res1.data()).hasSize(1);
        assertThat(res1.data().get(0).getCsName()).isEqualTo("Maya Putri");

        // Search Customer Name
        CallMonitoringFilterRequest req2 = CallMonitoringFilterRequest.builder().search("Budi").build();
        var res2 = repository.findWithPaging(req2, 1, 5, "cm.call_timestamp", "DESC");
        assertThat(res2.data()).hasSize(1);
        assertThat(res2.data().get(0).getCustomerName()).isEqualTo("Budi Santoso");

        // Search Call ID
        CallMonitoringFilterRequest req3 = CallMonitoringFilterRequest.builder().search("003").build();
        var res3 = repository.findWithPaging(req3, 1, 5, "cm.call_timestamp", "DESC");
        assertThat(res3.data()).hasSize(1);
        assertThat(res3.data().get(0).getCallId()).isEqualTo("CALL-20260801-003");
    }

    @Test
    @DisplayName("AC-5: Should filter by inclusive date range")
    void shouldFilterByInclusiveDateRange() {
        CallMonitoringFilterRequest request = CallMonitoringFilterRequest.builder()
                .startDate(LocalDate.of(2026, 7, 1))
                .endDate(LocalDate.of(2026, 7, 31))
                .build();

        var result = repository.findWithPaging(request, 1, 5, "cm.call_timestamp", "ASC");

        assertThat(result.totalRecords()).isEqualTo(2);
        assertThat(result.data()).hasSize(2);
    }

    @Test
    @DisplayName("AC-10: Should sort results ascending and descending")
    void shouldSortResults() {
        CallMonitoringFilterRequest request = CallMonitoringFilterRequest.builder().build();

        var ascResult = repository.findWithPaging(request, 1, 5, "cm.sentiment_score", "ASC");
        assertThat(ascResult.data().get(0).getSentimentScore()).isEqualByComparingTo("45.00");

        var descResult = repository.findWithPaging(request, 1, 5, "cm.sentiment_score", "DESC");
        assertThat(descResult.data().get(0).getSentimentScore()).isEqualByComparingTo("85.50");
    }

    @Test
    @DisplayName("AC-9: Should apply all active filters together (Period, Sentiment, and Search)")
    void shouldApplyCombinedFiltersTogether() {
        CallMonitoringFilterRequest request = CallMonitoringFilterRequest.builder()
                .startDate(LocalDate.of(2026, 7, 1))
                .endDate(LocalDate.of(2026, 8, 31))
                .sentiment("UNDER_70")
                .search("Dewi")
                .build();

        var result = repository.findWithPaging(request, 1, 5, "cm.call_timestamp", "DESC");

        assertThat(result.totalRecords()).isEqualTo(1);
        assertThat(result.data()).hasSize(1);
        assertThat(result.data().get(0).getCallId()).isEqualTo("CALL-20260715-002");
        assertThat(result.data().get(0).getCustomerName()).isEqualTo("Dewi Anggraini");
        assertThat(result.data().get(0).getSentimentScore()).isEqualByComparingTo("62.00");
    }

    @Test
    @DisplayName("AC-11: Should preserve active filters and sort order across pages")
    void shouldPreserveFiltersAndSortAcrossPages() {
        CallMonitoringFilterRequest request = CallMonitoringFilterRequest.builder()
                .startDate(LocalDate.of(2026, 7, 1))
                .endDate(LocalDate.of(2026, 8, 31))
                .build();

        // Page 1 (limit 2) sorted by sentiment ASC
        var page1 = repository.findWithPaging(request, 1, 2, "cm.sentiment_score", "ASC");
        assertThat(page1.data()).hasSize(2);
        assertThat(page1.totalRecords()).isEqualTo(4);
        assertThat(page1.data().get(0).getCallId()).isEqualTo("CALL-20260820-004"); // 45.00
        assertThat(page1.data().get(1).getCallId()).isEqualTo("CALL-20260715-002"); // 62.00

        // Page 2 (limit 2) with the exact same filters and sort order
        var page2 = repository.findWithPaging(request, 2, 2, "cm.sentiment_score", "ASC");
        assertThat(page2.data()).hasSize(2);
        assertThat(page2.totalRecords()).isEqualTo(4);
        assertThat(page2.data().get(0).getCallId()).isEqualTo("CALL-20260801-003"); // 70.00
        assertThat(page2.data().get(1).getCallId()).isEqualTo("CALL-20260701-001"); // 85.50
    }

    @Test
    @DisplayName("Should return empty result when no records match filter")
    void shouldReturnEmptyWhenNoMatches() {
        CallMonitoringFilterRequest request = CallMonitoringFilterRequest.builder()
                .search("NonExistentKeywordXYZ")
                .build();

        var result = repository.findWithPaging(request, 1, 5, "cm.call_timestamp", "DESC");

        assertThat(result.totalRecords()).isZero();
        assertThat(result.data()).isEmpty();
    }
}
