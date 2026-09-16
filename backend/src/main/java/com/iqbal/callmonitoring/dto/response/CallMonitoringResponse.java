package com.iqbal.callmonitoring.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CallMonitoringResponse {

    private String callId;
    private OffsetDateTime callTimestamp;
    private String csName;
    private String customerName;
    private BigDecimal sentimentScore;
}
