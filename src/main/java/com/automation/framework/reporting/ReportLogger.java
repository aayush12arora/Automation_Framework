package com.automation.framework.reporting;

import com.aventstack.extentreports.Status;
import com.automation.framework.utilities.ScreenshotUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.message.ParameterizedMessage;
import org.testng.Reporter;

/**
 * One-stop façade for step reporting. Every method writes to BOTH the Log4j log
 * and the current test's Extent report node, so a step stays a single call from
 * anywhere in the framework (tests, page objects, flows).
 *
 * <p>Messages support Log4j-style {@code {}} placeholders, e.g.
 * {@code ReportLogger.info("Clicked {}", locator)}.
 *
 * <p>All methods are null-safe: if no Extent test is active on this thread (for
 * example when a page object is exercised outside a {@code @Test}), the message
 * is still logged to the console and the report call is quietly skipped.
 */
public final class ReportLogger {

    private static final Logger log = LogManager.getLogger(ReportLogger.class);

    private ReportLogger() {
    }

    // ------------------------------------------------------------------- logging

    /** Informational step, e.g. {@code ReportLogger.info("Clicked {}", locator)}. */
    public static void info(String message, Object... args) {
        String text = format(message, args);
        log.info(text);
        Reporter.log(text);
        ExtentTestManager.info(text);
    }

    public static void pass(String message, Object... args) {
        String text = format(message, args);
        log.info(text);
        Reporter.log(text);
        ExtentTestManager.pass(text);
    }

    public static void fail(String message, Object... args) {
        String text = format(message, args);
        log.error(text);
        Reporter.log(text);
        ExtentTestManager.fail(text);
    }

    public static void warn(String message, Object... args) {
        String text = format(message, args);
        log.warn(text);
        Reporter.log(text);
        ExtentTestManager.warning(text);
    }

    // --------------------------------------------------------------- screenshots

    /**
     * Capture the current browser screen and attach it to the report at this
     * step as an INFO entry. Safe to call when no driver exists (the step is
     * logged without an image).
     */
    public static void screenshot(String message, Object... args) {
        screenshot(Status.INFO, message, args);
    }

    /** Capture the current screen and attach it under the given status. */
    public static void screenshot(Status status, String message, Object... args) {
        String text = format(message, args);
        log.info("[screenshot] {}", text);
        ExtentTestManager.attachScreenshot(status, text, ScreenshotUtils.captureBase64());
    }

    private static String format(String message, Object... args) {
        if (message == null || args == null || args.length == 0) {
            return message;
        }
        return ParameterizedMessage.format(message, args);
    }
}
