package com.automation.framework.api.soap;

import com.automation.framework.exceptions.FrameworkException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

/**
 * Generic SOAP executor: POSTs a raw SOAP envelope to an endpoint and returns
 * the response body. Application-agnostic - no WSDL-specific or domain-specific
 * bindings - so it works for any SOAP service by supplying the envelope XML.
 */
public class SoapAPIExecutor {

    private static final Logger log = LogManager.getLogger(SoapAPIExecutor.class);

    private final HttpClient client = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(30))
            .build();

    /**
     * Send a SOAP request.
     *
     * @param endpoint       full service URL
     * @param soapAction     value for the SOAPAction header (may be empty)
     * @param soapEnvelope   the complete SOAP envelope XML
     * @return the raw response body
     */
    public String send(String endpoint, String soapAction, String soapEnvelope) {
        log.info("SOAP POST {} (action={})", endpoint, soapAction);
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(endpoint))
                    .timeout(Duration.ofSeconds(60))
                    .header("Content-Type", "text/xml; charset=utf-8")
                    .header("SOAPAction", soapAction == null ? "" : soapAction)
                    .POST(HttpRequest.BodyPublishers.ofString(soapEnvelope))
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            return response.body();
        } catch (Exception e) {
            throw new FrameworkException("SOAP request to " + endpoint + " failed", e);
        }
    }
}
