package com.iqbal.callmonitoring.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class WebResponse<T> {

    private int code;
    private String message;
    private T data;
    private PaginationMeta meta;

    public static <T> WebResponse<T> success(T data, PaginationMeta meta) {
        return WebResponse.<T>builder()
                .code(200)
                .message(Success)
                .data(data)
                .meta(meta)
                .build();
    }

    public static <T> WebResponse<T> success(T data) {
        return success(data, null);
    }
}
