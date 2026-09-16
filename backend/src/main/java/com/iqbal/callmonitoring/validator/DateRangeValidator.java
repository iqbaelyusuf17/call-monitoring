package com.iqbal.callmonitoring.validator;

import com.iqbal.callmonitoring.exception.BadRequestException;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class DateRangeValidator {

    private static final int MAX_MONTHS = 3;

    public void validate(LocalDate startDate, LocalDate endDate) {
        if (startDate == null && endDate == null) {
            return;
        }

        if (startDate != null && endDate == null) {
            endDate = LocalDate.now();
        } else if (startDate == null) {
            startDate = endDate.minusMonths(MAX_MONTHS);
        }

        if (startDate.isAfter(endDate)) {
            throw new BadRequestException("Start period cannot be after end period");
        }

        // Check if range spans more than 3 months
        if (startDate.plusMonths(MAX_MONTHS).isBefore(endDate)) {
            throw new BadRequestException("Period range cannot exceed 3 months");
        }

        // Check if period is older than the latest 3 months from today
        LocalDate earliestAllowedDate = LocalDate.now().minusMonths(MAX_MONTHS);
        if (startDate.isBefore(earliestAllowedDate)) {
            throw new BadRequestException("Selectable period is limited to the latest 3 months");
        }
    }
}
