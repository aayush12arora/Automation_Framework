package com.automation.framework.core.base;

import com.automation.framework.core.config.ApplicationConfig;
import com.automation.framework.core.config.ConfigurationManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.annotations.BeforeSuite;

/**
 * Root of all test classes. Holds shared access to configuration and is the
 * place to put lifecycle hooks that apply to both UI and API tests.
 * Reporting is driven by {@link com.automation.framework.reporting.TestListener}
 * (registered in {@code testng.xml}), so no report wiring is needed here.
 */
public abstract class BaseTest {

    protected final Logger log = LogManager.getLogger(getClass());
    protected final ConfigurationManager config = ConfigurationManager.getInstance();

    @BeforeSuite(alwaysRun = true)
    public void logSuiteContext() {
        log.info("==============================================================");
        log.info(" Application : {}", config.getApplicationName());
        log.info(" Environment : {}", config.getEnvironmentName());
        log.info("==============================================================");
    }

    protected ApplicationConfig application() {
        return config.getApplicationConfig();
    }
}
