package com.iqbal.callmonitoring.repository.base;

/**
 * Interface generik untuk repository yang mendukung operasi Native Paging dan Count.
 *
 * @param <T> Tipe entitas / domain model
 * @param <F> Tipe filter request DTO
 */
public interface BasePagingRepository<T, F> {

    /**
     * Mengambil daftar data dan total records secara terpadu dalam 1 panggilan.
     */
    PagingResult<T> findWithPaging(
            F filterRequest,
            int page,
            int limit,
            String sortColumn,
            String sortDirection
    );
}
