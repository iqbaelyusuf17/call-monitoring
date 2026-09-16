package com.iqbal.callmonitoring.repository.base;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.util.Collections;
import java.util.List;

/**
 * Abstraksi Base Repository untuk Pure Spring JDBC.
 * Menyediakan mekanisme paging terpadu tanpa JPA/Hibernate,
 * dilengkapi dynamic sorting, kalkulasi offset, dan dukungan custom count query.
 */
@Slf4j
@RequiredArgsConstructor
public abstract class AbstractJdbcRepository {

    protected final NamedParameterJdbcTemplate jdbcTemplate;

    /**
     * Hook method: Jika turunan repository membutuhkan custom count query
     * (misal: COUNT(DISTINCT ...)), cukup override method ini.
     */
    protected String getCustomCountQuery(String countExpr, CharSequence queryFrom) {
        return null;
    }

    /**
     * Eksekusi query paging secara efisien dengan parameterized SQL.
     */
    protected <T> PagingResult<T> executePagingResult(
            String countExpr,
            String selectColumns,
            CharSequence queryFrom,
            MapSqlParameterSource params,
            int page,
            int limit,
            String sortColumn,
            String sortDirection,
            RowMapper<T> rowMapper
    ) {
        int safePage = Math.max(1, page);
        int safeLimit = Math.max(1, limit);
        int offset = (safePage - 1) * safeLimit;

        // 1. Eksekusi Count Query
        String countSql = getCustomCountQuery(countExpr, queryFrom);
        if (countSql == null || countSql.isBlank()) {
            countSql = SELECT  + countExpr +   + queryFrom;
        }

        log.debug(Executing Count Query: {} with params: {}, countSql, params.getValues());
        Long totalRecordsObj = jdbcTemplate.queryForObject(countSql, params, Long.class);
        long totalRecords = totalRecordsObj != null ? totalRecordsObj : 0L;

        if (totalRecords == 0L) {
            return new PagingResult<>(Collections.emptyList(), 0L, safePage, safeLimit, 0);
        }

        int totalPages = (int) Math.ceil((double) totalRecords / safeLimit);

        // 2. Susun Data Query dengan ORDER BY dan LIMIT OFFSET
        StringBuilder dataSql = new StringBuilder()
                .append(SELECT ).append(selectColumns).append( )
                .append(queryFrom);

        if (sortColumn != null && !sortColumn.isBlank()) {
            String dir = ASC.equalsIgnoreCase(sortDirection) ? ASC : DESC;
            dataSql.append( ORDER BY ).append(sortColumn).append( ).append(dir);
        }

        dataSql.append( LIMIT :limit OFFSET :offset);

        MapSqlParameterSource dataParams = new MapSqlParameterSource(params.getValues());
        dataParams.addValue(limit, safeLimit);
        dataParams.addValue(offset, offset);

        log.debug(Executing Data Query: {} with params: {}, dataSql, dataParams.getValues());
        List<T> data = jdbcTemplate.query(dataSql.toString(), dataParams, rowMapper);

        return new PagingResult<>(data, totalRecords, safePage, safeLimit, totalPages);
    }
}
