package com.automation.framework.reporting;

import com.aventstack.extentreports.ExtentTest;

/**
 * Mix-in that gives step logging and screenshots to any class that implements it,
 * so the same one-liners are available in tests <em>and</em> page objects.
 *
 * <p>{@code BaseTest} and {@code BasePage} both implement this, meaning every test
 * class and every page object inherits {@link #logStep}, {@link #attachScreenshot}
 * and friends for free. Messages support Log4j-style {@code {}} placeholders:
 * {@snippet : logStep("Available course: {}", element.getText()); }
 *
 * <p>All methods are null-safe: outside a running test they still log to the
 * console and quietly skip the report.
 */
public interface StepReporter {

    /** The Extent report node for the test running on this thread (may be null). */
    default ExtentTest report() {
        return ExtentTestManager.getTest();
    }

    /** Informational step written to BOTH the console and the HTML report. */
    default void logStep(String message, Object... args) {
        ReportLogger.info(message, args);
    }

    /** Passing step (green) written to the console and the report. */
    default void logPass(String message, Object... args) {
        ReportLogger.pass(message, args);
    }

    /** Failing step (red) written to the console and the report. */
    default void logFail(String message, Object... args) {
        ReportLogger.fail(message, args);
    }

    /** Warning step (amber) written to the console and the report. */
    default void logWarning(String message, Object... args) {
        ReportLogger.warn(message, args);
    }

    /**
     * Capture the current browser screen and pin it to the report at this step.
     * Works from tests and page objects alike (it uses the thread's active
     * driver); if no driver exists the step is logged without an image.
     */
    default void attachScreenshot(String message, Object... args) {
        ReportLogger.screenshot(message, args);
    }
}
