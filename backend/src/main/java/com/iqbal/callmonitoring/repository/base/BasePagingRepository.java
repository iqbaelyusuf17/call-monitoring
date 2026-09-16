package com.iqbal.callmonitoring.repository.base;

public interface BasePagingRepository<T, R> {

    PagingResult<T> findWithPaging(
            R request,
            int page,
            int limit,
            String sortColumn,
            String sortDirection
    );
}
