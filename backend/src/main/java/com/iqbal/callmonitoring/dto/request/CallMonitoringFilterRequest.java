package com.iqbal.callmonitoring.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CallMonitoringFilterRequest {

    private String search;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate startDate;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate endDate;

    /**
     * Kategori sentimen:
     * - ALL / null: Semua sentimen
     * - UNDER_70: sentiment_score < 70%
     * - 70_AND_ABOVE: sentiment_score >= 70%
     */
    private String sentiment;

    @Builder.Default
    private int page = 1;

    @Builder.Default
    private int limit = 10;

    @Builder.Default
    private String sortBy = call_timestamp;

    @Builder.Default
    private String sortOrder = desc;
}
