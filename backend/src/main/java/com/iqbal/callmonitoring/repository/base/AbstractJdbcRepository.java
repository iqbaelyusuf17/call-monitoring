package com.iqbal.callmonitoring.repository.base;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.util.Collections;
import java.util.List;

/**
 * Abstract Base Repository untuk mengeksekusi Native SQL Paging & Count secara reusable.
 * Class anak (child) tetap memegang kendali penuh atas definisi queryFrom (FROM, JOIN, WHERE).
 */
@Slf4j
public abstract class AbstractJdbcRepository {

    protected final NamedParameterJdbcTemplate jdbcTemplate;

    protected AbstractJdbcRepository(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * Mengeksekusi query COUNT(*) default dengan parameter terikat secara aman.
     */
    protected long executeCount(CharSequence queryFrom, MapSqlParameterSource params) {
        return executeCount("COUNT(*)", queryFrom, params);
    }

    /**
     * Mengeksekusi query count dengan ekspresi kustom, misal COUNT(DISTINCT cm.call_id).
     */
    protected long executeCount(String countExpression, CharSequence queryFrom, MapSqlParameterSource params) {
        String countSql = "SELECT " + countExpression + " " + queryFrom;
        log.debug("Executing native count query: {} with params: {}", countSql, params.getValues());

        Long count = jdbcTemplate.queryForObject(countSql, params, Long.class);
        return count != null ? count : 0L;
    }

    /**
     * Mengeksekusi query data dengan LIMIT dan OFFSET.
     */
    protected <T> List<T> executePaging(
            String selectColumns,
            CharSequence queryFrom,
            MapSqlParameterSource params,
            int offset,
            int limit,
            String sortColumn,
            String sortDirection,
            RowMapper<T> rowMapper
    ) {
        String dataSql = String.format(
                "SELECT %s %s ORDER BY %s %s LIMIT :limit OFFSET :offset",
                selectColumns, queryFrom, sortColumn, sortDirection
        );

        params.addValue("limit", limit);
        params.addValue("offset", offset);

        log.debug("Executing native paging query: {} with params: {}", dataSql, params.getValues());
        return jdbcTemplate.query(dataSql, params, rowMapper);
    }

    /**
     * Mengeksekusi query COUNT(*) dan Paging Data secara terpadu.
     * Mengoptimasi performa dengan melewati query data jika total records = 0.
     */
    protected <T> PagingResult<T> executePagingResult(
            String selectColumns,
            CharSequence queryFrom,
            MapSqlParameterSource params,
            int page,
            int limit,
            String sortColumn,
            String sortDirection,
            RowMapper<T> rowMapper
    ) {
        return executePagingResult("COUNT(*)", selectColumns, queryFrom, params, page, limit, sortColumn, sortDirection, rowMapper);
    }

    /**
     * Mengeksekusi query count kustom (misal COUNT(DISTINCT cm.call_id)) dan Paging Data secara terpadu.
     */
    protected <T> PagingResult<T> executePagingResult(
            String countExpression,
            String selectColumns,
            CharSequence queryFrom,
            MapSqlParameterSource params,
            int page,
            int limit,
            String sortColumn,
            String sortDirection,
            RowMapper<T> rowMapper
    ) {
        long total = executeCount(countExpression, queryFrom, params);
        if (total == 0) {
            return new PagingResult<>(Collections.emptyList(), 0L);
        }

        int offset = Math.max(0, (page - 1) * limit);
        List<T> data = executePaging(selectColumns, queryFrom, params, offset, limit, sortColumn, sortDirection, rowMapper);
        return new PagingResult<>(data, total);
    }
}
