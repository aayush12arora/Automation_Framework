package com.automation.framework.api.models.response;

import com.automation.framework.api.models.BaseAPIModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Generic API response model: status code, headers, and raw body. Domain models
 * can extend {@link BaseAPIModel} for typed deserialization instead.
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class ApiResponse extends BaseAPIModel {

    private int statusCode;
    private Map<String, String> headers = new LinkedHashMap<>();
    private String body;

    public boolean isSuccessful() {
        return statusCode >= 200 && statusCode < 300;
    }
}
