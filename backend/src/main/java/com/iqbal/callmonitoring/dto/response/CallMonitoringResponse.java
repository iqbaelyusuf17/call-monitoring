package com.iqbal.callmonitoring.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
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

    @JsonProperty("call_id")
    private String callId;

    @JsonProperty("call_timestamp")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
    private OffsetDateTime callTimestamp;

    @JsonProperty("cs_name")
    private String csName;

    @JsonProperty("customer_name")
    private String customerName;

    @JsonProperty("sentiment_score")
    private BigDecimal sentimentScore;
}
