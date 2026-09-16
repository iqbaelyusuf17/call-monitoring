package com.iqbal.callmonitoring.repository.base;

import java.util.List;

/**
 * Record generik untuk membungkus data list dan total records hasil query paging.
 */
public record PagingResult<T>(
        List<T> data,
        long totalRecords
) {
}
