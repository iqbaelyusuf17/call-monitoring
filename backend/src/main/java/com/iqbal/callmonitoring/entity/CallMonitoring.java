package com.iqbal.callmonitoring.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CallMonitoring {

    private String callId;
    private OffsetDateTime callTimestamp;
    private String csName;
    private String customerName;
    private BigDecimal sentimentScore;
}
