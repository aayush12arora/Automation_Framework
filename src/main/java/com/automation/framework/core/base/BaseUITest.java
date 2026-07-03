package com.automation.framework.core.base;

import com.automation.framework.core.driver.DriverManager;
import com.automation.framework.engine.Flow;
import com.automation.framework.engine.FlowExecutor;
import com.automation.framework.pages.DynamicPage;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

/**
 * Base class for browser-based tests. Creates a fresh {@link WebDriver} per test
 * method (thread-local, so parallel-safe) and tears it down afterward. Provides
 * convenience accessors for config-driven pages and flow execution.
 */
public abstract class BaseUITest extends BaseTest {

    @BeforeMethod(alwaysRun = true)
    public void startDriver() {
        DriverManager.initDriver();
        log.info("Driver started for test");
    }

    @AfterMethod(alwaysRun = true)
    public void stopDriver() {
        DriverManager.quitDriver();
        log.info("Driver stopped for test");
    }

    protected WebDriver driver() {
        return DriverManager.getDriver();
    }

    /** Navigate the browser to the active environment's base URL. */
    protected void openBaseUrl() {
        driver().get(config.getBaseUrl());
    }

    /** Config-driven page object for the named page. */
    protected DynamicPage page(String pageName) {
        return new DynamicPage(driver(), pageName);
    }

    /** Run a flow defined on the classpath (JSON/YAML). */
    protected void runFlow(String flowResource) {
        Flow flow = Flow.fromClasspath(flowResource);
        new FlowExecutor(driver()).execute(flow);
    }

    /** Run an already-loaded flow. */
    protected void runFlow(Flow flow) {
        new FlowExecutor(driver()).execute(flow);
    }
}
