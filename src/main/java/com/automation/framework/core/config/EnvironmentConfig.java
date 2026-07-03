package com.automation.framework.core.config;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * One deployment environment of an application (e.g. dev, stage, prod).
 * Holds the web base URL, optional API base URL, and an open-ended
 * {@code properties} map for anything else the application needs.
 */
@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class EnvironmentConfig {

    /** Base web URL, e.g. "https://www.gyansathi.com". */
    private String baseUrl;

    /** Optional REST API base URL for API tests. */
    private String apiBaseUrl;

    /** Arbitrary extra key/value pairs available to tests and flows. */
    private Map<String, String> properties = new LinkedHashMap<>();

    public String property(String key) {
        return properties.get(key);
    }
}
