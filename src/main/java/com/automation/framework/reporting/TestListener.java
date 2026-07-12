package com.automation.framework.reporting;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

/**
 * Bridges TestNG lifecycle events into the Extent report. Each executed test
 * becomes exactly one report node named {@code Class.method}; all of that test's
 * step logs and screenshots are nested underneath it.
 *
 * <p>Registered globally via {@code testng.xml} so every suite gets reporting.
 *
 * <p>Screenshots are intentionally NOT captured here. TestNG runs {@code @AfterMethod}
 * (which quits the driver) <em>before</em> these listener callbacks, so a driver
 * no longer exists by the time we are called. The pass/failure screenshot is
 * therefore taken in {@code BaseUITest} while the driver is still alive; this
 * listener only records the outcome text and manages the report node.
 */
public class TestListener implements ITestListener {

    private static final Logger log = LogManager.getLogger(TestListener.class);

    @Override
    public void onTestStart(ITestResult result) {
        ExtentTestManager.startTest(testName(result), description(result));
        log.info("START  {}", testName(result));
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        ExtentTestManager.pass("Test passed");
        log.info("PASS   {}", testName(result));
        ExtentTestManager.remove();
    }

    @Override
    public void onTestFailure(ITestResult result) {
        Throwable error = result.getThrowable();
        log.error("FAIL   {} : {}", testName(result), message(error));
        ExtentTestManager.fail("Test failed: " + message(error));
        if (error != null) {
            ExtentTestManager.getTest().fail(error);
        }
        ExtentTestManager.remove();
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        // TestNG does not fire onTestStart for skipped tests, so create the node here.
        if (ExtentTestManager.getTest() == null) {
            ExtentTestManager.startTest(testName(result), description(result));
        }
        ExtentTestManager.skip("Test skipped: " + message(result.getThrowable()));
        log.warn("SKIP   {}", testName(result));
        ExtentTestManager.remove();
    }

    @Override
    public void onFinish(ITestContext context) {
        ExtentManager.flush();
    }

    private static String testName(ITestResult result) {
        return result.getTestClass().getRealClass().getSimpleName()
                + "." + result.getMethod().getMethodName();
    }

    private static String description(ITestResult result) {
        String description = result.getMethod().getDescription();
        return description == null ? "" : description;
    }

    private static String message(Throwable error) {
        if (error == null) {
            return "unknown error";
        }
        return error.getMessage() == null ? error.toString() : error.getMessage();
    }
}
