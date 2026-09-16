package com.iqbal.callmonitoring.repository.base;

import java.util.List;

public record PagingResult<T>(
        List<T> data,
        long totalRecords,
        int page,
        int limit,
        int totalPages
) {
}
