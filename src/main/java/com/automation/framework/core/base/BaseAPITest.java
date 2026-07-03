package com.automation.framework.core.base;

import com.automation.framework.api.executors.RestAPIExecutor;
import io.restassured.RestAssured;
import org.testng.annotations.BeforeClass;

/**
 * Base class for API tests. Configures REST-Assured with the active
 * environment's API base URL and exposes a ready-to-use {@link RestAPIExecutor}.
 */
public abstract class BaseAPITest extends BaseTest {

    protected RestAPIExecutor restClient;

    @BeforeClass(alwaysRun = true)
    public void initApi() {
        String apiBaseUrl = config.getApiBaseUrl();
        if (apiBaseUrl != null && !apiBaseUrl.isBlank()) {
            RestAssured.baseURI = apiBaseUrl;
        }
        restClient = new RestAPIExecutor(apiBaseUrl);
        log.info("API base URL: {}", apiBaseUrl);
    }
}
