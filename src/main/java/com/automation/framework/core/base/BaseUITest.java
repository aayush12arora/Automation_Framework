package com.automation.framework.core.base;

import com.aventstack.extentreports.Status;
import com.automation.framework.core.driver.DriverManager;
import com.automation.framework.core.factory.PageFactory;
import com.automation.framework.engine.Flow;
import com.automation.framework.engine.FlowExecutor;
import com.automation.framework.pages.DynamicPage;
import com.automation.framework.reporting.ExtentTestManager;
import com.automation.framework.utilities.ScreenshotUtils;
import org.openqa.selenium.WebDriver;
import org.testng.ITestResult;
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

    /**
     * Captures the result screenshot <em>before</em> the driver is quit and then
     * shuts the driver down. This ordering matters: TestNG runs {@code @AfterMethod}
     * before the {@code ITestListener} callbacks, so the failure/pass screenshot
     * must be taken here while a live session still exists.
     */
    @AfterMethod(alwaysRun = true)
    public void stopDriver(ITestResult result) {
        try {
            captureResultScreenshot(result);
        } finally {
            DriverManager.quitDriver();
            log.info("Driver stopped for test");
        }
    }

    protected WebDriver driver() {
        return DriverManager.getDriver();
    }

    /** Navigate the browser to the active environment's base URL. */
    protected void openBaseUrl() {
        driver().get(config.getBaseUrl());
    }

    // attachScreenshot(...) is inherited from StepReporter (via BaseTest).

    /** Hand-written page object, e.g. {@code pageObject(HomePage.class)}. */
    protected <T extends BasePage> T pageObject(Class<T> pageClass) {
        return PageFactory.custom(pageClass, driver());
    }

    /** Config-driven page object for the named page. Backs the YAML flow engine. */
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

    // ------------------------------------------------------------------ reporting

    private void captureResultScreenshot(ITestResult result) {
        if (!DriverManager.hasDriver()) {
            return;
        }
        if (result.getStatus() == ITestResult.FAILURE && config.screenshotOnFailure()) {
            // Keep a file copy for CI artifacts, and embed the image in the report.
            ScreenshotUtils.capture(result.getMethod().getMethodName());
            ExtentTestManager.attachScreenshot(Status.FAIL, "Screenshot at failure",
                    ScreenshotUtils.captureBase64());
        } else if (result.getStatus() == ITestResult.SUCCESS && config.screenshotOnPass()) {
            ExtentTestManager.attachScreenshot(Status.PASS, "Screenshot at success",
                    ScreenshotUtils.captureBase64());
        }
    }
}
