package com.iqbal.callmonitoring.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaginationMeta {

    private int page;
    private int limit;

    @JsonProperty("total_records")
    private long totalRecords;

    @JsonProperty("total_pages")
    private int totalPages;

    @JsonProperty("has_previous")
    private boolean hasPrevious;

    @JsonProperty("has_next")
    private boolean hasNext;

    public static PaginationMeta of(int page, int limit, long totalRecords) {
        int totalPages = limit > 0 ? (int) Math.ceil((double) totalRecords / limit) : 0;
        return PaginationMeta.builder()
                .page(page)
                .limit(limit)
                .totalRecords(totalRecords)
                .totalPages(totalPages)
                .hasPrevious(page > 1)
                .hasNext(page < totalPages)
                .build();
    }
}
