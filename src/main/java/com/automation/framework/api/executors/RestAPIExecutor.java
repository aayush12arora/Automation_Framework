package com.automation.framework.api.executors;

import com.automation.framework.core.config.ConfigurationManager;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Collections;
import java.util.Map;

/**
 * A thin, application-agnostic REST-Assured wrapper. The base URI defaults to
 * the active environment's {@code apiBaseUrl}, but any absolute path works too.
 * Kept deliberately generic so it fits any API without payment/biller specifics.
 */
public class RestAPIExecutor {

    private static final Logger log = LogManager.getLogger(RestAPIExecutor.class);

    private final String baseUri;
    private Map<String, String> defaultHeaders = Collections.emptyMap();

    public RestAPIExecutor() {
        this(ConfigurationManager.getInstance().getApiBaseUrl());
    }

    public RestAPIExecutor(String baseUri) {
        this.baseUri = baseUri;
    }

    public RestAPIExecutor withDefaultHeaders(Map<String, String> headers) {
        this.defaultHeaders = headers == null ? Collections.emptyMap() : headers;
        return this;
    }

    private RequestSpecification request(Map<String, String> headers) {
        RequestSpecification spec = RestAssured.given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON);
        if (baseUri != null && !baseUri.isBlank()) {
            spec.baseUri(baseUri);
        }
        spec.headers(defaultHeaders);
        if (headers != null && !headers.isEmpty()) {
            spec.headers(headers);
        }
        return spec.log().ifValidationFails();
    }

    public Response get(String endpoint, Map<String, String> headers) {
        log.info("GET {}", endpoint);
        return request(headers).get(endpoint);
    }

    public Response post(String endpoint, Object body, Map<String, String> headers) {
        log.info("POST {}", endpoint);
        return request(headers).body(body == null ? "" : body).post(endpoint);
    }

    public Response put(String endpoint, Object body, Map<String, String> headers) {
        log.info("PUT {}", endpoint);
        return request(headers).body(body == null ? "" : body).put(endpoint);
    }

    public Response delete(String endpoint, Map<String, String> headers) {
        log.info("DELETE {}", endpoint);
        return request(headers).delete(endpoint);
    }
}
