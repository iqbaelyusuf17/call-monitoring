package com.iqbal.callmonitoring.dto.request;

import lombok.Builder;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.BindParam;

import java.time.LocalDate;

@Builder
public record CallMonitoringFilterRequest(
    Integer page,
    Integer limit,
    String search,
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @BindParam("start_date")
    LocalDate startDate,
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @BindParam("end_date")
    LocalDate endDate,
    String sentiment,
    @BindParam("sort_by")
    String sortBy,
    @BindParam("sort_order")
    String sortOrder
) {
    public CallMonitoringFilterRequest {
        if (page == null || page < 1) page = 1;
        if (limit == null || limit < 1) limit = 5;
        if (sortBy == null || sortBy.isBlank()) sortBy = "call_timestamp";
        if (sortOrder == null || sortOrder.isBlank()) sortOrder = "desc";
    }

    public Integer getPage() { return page(); }
    public Integer getLimit() { return limit(); }
    public String getSearch() { return search(); }
    public LocalDate getStartDate() { return startDate(); }
    public LocalDate getEndDate() { return endDate(); }
    public String getSentiment() { return sentiment(); }
    public String getSortBy() { return sortBy(); }
    public String getSortOrder() { return sortOrder(); }
}
