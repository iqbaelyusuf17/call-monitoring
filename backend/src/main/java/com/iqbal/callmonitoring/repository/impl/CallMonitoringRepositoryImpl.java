package com.iqbal.callmonitoring.repository.impl;

import com.iqbal.callmonitoring.dto.request.CallMonitoringFilterRequest;
import com.iqbal.callmonitoring.entity.CallMonitoring;
import com.iqbal.callmonitoring.repository.CallMonitoringRepository;
import com.iqbal.callmonitoring.repository.base.AbstractJdbcRepository;
import com.iqbal.callmonitoring.repository.base.PagingResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

@Slf4j
@Repository
public class CallMonitoringRepositoryImpl extends AbstractJdbcRepository implements CallMonitoringRepository {

    private static final String SELECT_COLUMNS = cm.call_id, cm.call_timestamp, cm.cs_name, cm.customer_name, cm.sentiment_score;

    public CallMonitoringRepositoryImpl(NamedParameterJdbcTemplate jdbcTemplate) {
        super(jdbcTemplate);
    }

    private static final RowMapper<CallMonitoring> ROW_MAPPER = (rs, rowNum) -> {
        OffsetDateTime timestamp = null;
        Object tsObj = rs.getObject(call_timestamp);
        if (tsObj instanceof OffsetDateTime odt) {
            timestamp = odt;
        } else if (tsObj instanceof Timestamp ts) {
            timestamp = ts.toInstant().atOffset(ZoneOffset.UTC);
        } else if (tsObj != null) {
            timestamp = OffsetDateTime.parse(tsObj.toString());
        }

        return CallMonitoring.builder()
                .callId(rs.getString(call_id))
                .callTimestamp(timestamp)
                .csName(rs.getString(cs_name))
                .customerName(rs.getString(customer_name))
                .sentimentScore(rs.getBigDecimal(sentiment_score))
                .build();
    };

    /**
     * Membangun klausa FROM dan kondisi WHERE dinamis dengan parameterized SQL.
     */
    private StringBuilder buildQueryFrom(CallMonitoringFilterRequest request, MapSqlParameterSource params) {
        StringBuilder queryFrom = new StringBuilder("
            FROM call_monitorings cm
            WHERE TRUE
        ");

        if (request == null) {
            return queryFrom;
        }

        // 1. Filter periode (Inclusive)
        if (request.getStartDate() != null) {
            queryFrom.append( AND cm.call_timestamp >= :startDate);
            params.addValue(startDate, request.getStartDate().atStartOfDay().atOffset(ZoneOffset.UTC));
        }
        if (request.getEndDate() != null) {
            queryFrom.append( AND cm.call_timestamp <= :endDate);
            params.addValue(endDate, request.getEndDate().atTime(LocalTime.MAX).atOffset(ZoneOffset.UTC));
        }

        // 2. Filter sentimen (null-safe)
        if (UNDER_70.equalsIgnoreCase(request.getSentiment())) {
            queryFrom.append( AND (cm.sentiment_score IS NOT NULL AND cm.sentiment_score < 70.00));
        } else if (70_AND_ABOVE.equalsIgnoreCase(request.getSentiment())) {
            queryFrom.append( AND (cm.sentiment_score IS NOT NULL AND cm.sentiment_score >= 70.00));
        }

        // 3. Pencarian kata kunci global (null-safe)
        if (request.getSearch() != null && !request.getSearch().trim().isEmpty()) {
            queryFrom.append("
                 AND (
                    LOWER(cm.call_id) LIKE :search
                    OR (cm.cs_name IS NOT NULL AND LOWER(cm.cs_name) LIKE :search)
                    OR (cm.customer_name IS NOT NULL AND LOWER(cm.customer_name) LIKE :search)
                 )
            ");
            params.addValue(search, % + request.getSearch().trim().toLowerCase() + %);
        }

        return queryFrom;
    }

    @Override
    public PagingResult<CallMonitoring> findWithPaging(
            CallMonitoringFilterRequest request,
            int page,
            int limit,
            String sortColumn,
            String sortDirection
    ) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        StringBuilder queryFrom = buildQueryFrom(request, params);
        return executePagingResult(COUNT(*), SELECT_COLUMNS, queryFrom, params, page, limit, sortColumn, sortDirection, ROW_MAPPER);
    }
}
