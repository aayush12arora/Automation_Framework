package com.automation.framework.core.base;

import com.automation.framework.api.RestClient;
import io.restassured.RestAssured;
import org.testng.annotations.BeforeClass;

/**
 * Base class for API tests. Configures REST-Assured with the active
 * environment's API base URL and exposes a ready-to-use {@link RestClient}.
 */
public abstract class BaseAPITest extends BaseTest {

    protected RestClient restClient;

    @BeforeClass(alwaysRun = true)
    public void initApi() {
        String apiBaseUrl = config.getApiBaseUrl();
        if (apiBaseUrl != null && !apiBaseUrl.isBlank()) {
            RestAssured.baseURI = apiBaseUrl;
        }
        restClient = new RestClient(apiBaseUrl);
        log.info("API base URL: {}", apiBaseUrl);
    }
}
