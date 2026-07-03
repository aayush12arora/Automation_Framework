package com.automation.framework.api.models.request;

import com.automation.framework.api.models.BaseAPIModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Generic API request model: endpoint, HTTP method, headers, and a free-form
 * body. Domain-specific requests can extend {@link BaseAPIModel} with typed
 * fields; this class covers the common case without any domain assumptions.
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class ApiRequest extends BaseAPIModel {

    private String endpoint;
    private String method = "GET";
    private Map<String, String> headers = new LinkedHashMap<>();
    private Object body;
}
