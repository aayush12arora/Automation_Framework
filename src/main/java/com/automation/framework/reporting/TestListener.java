package com.automation.framework.reporting;

import com.aventstack.extentreports.Status;
import com.automation.framework.core.config.ConfigurationManager;
import com.automation.framework.utilities.ScreenshotUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

/**
 * Bridges TestNG lifecycle events into the Extent report: starts a report node
 * per test, records pass/fail/skip, and embeds a screenshot on failure.
 * Registered globally via {@code testng.xml} so every suite gets reporting.
 */
public class TestListener implements ITestListener {

    private static final Logger log = LogManager.getLogger(TestListener.class);

    @Override
    public void onTestStart(ITestResult result) {
        String description = result.getMethod().getDescription();
        ExtentTestManager.startTest(testName(result),
                description == null ? "" : description);
        log.info("START  {}", testName(result));
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        if (ConfigurationManager.getInstance().screenshotOnPass()) {
            ExtentTestManager.attachScreenshot(Status.PASS, "Passed", ScreenshotUtils.captureBase64());
        } else {
            ExtentTestManager.pass("Passed");
        }
        log.info("PASS   {}", testName(result));
    }

    @Override
    public void onTestFailure(ITestResult result) {
        Throwable error = result.getThrowable();
        log.error("FAIL   {} : {}", testName(result), error == null ? "" : error.getMessage());
        String base64 = ConfigurationManager.getInstance().screenshotOnFailure()
                ? ScreenshotUtils.captureBase64()
                : null;
        // Persist a file copy too for CI artifacts.
        ScreenshotUtils.capture(result.getMethod().getMethodName());
        ExtentTestManager.attachScreenshot(Status.FAIL,
                "Failed: " + (error == null ? "unknown error" : error.getMessage()), base64);
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        ExtentTestManager.skip("Skipped: "
                + (result.getThrowable() == null ? "" : result.getThrowable().getMessage()));
        log.warn("SKIP   {}", testName(result));
    }

    @Override
    public void onFinish(ITestContext context) {
        ExtentManager.flush();
        ExtentTestManager.remove();
    }

    private static String testName(ITestResult result) {
        return result.getTestClass().getRealClass().getSimpleName()
                + "." + result.getMethod().getMethodName();
    }
}
