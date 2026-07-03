package com.automation.framework.reporting;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Convenience façade that writes a message to BOTH the Log4j log and the current
 * Extent report node in one call, so step logging stays a one-liner in tests.
 */
public final class ReportLogger {

    private static final Logger log = LogManager.getLogger(ReportLogger.class);

    private ReportLogger() {
    }

    public static void info(String message) {
        log.info(message);
        ExtentTestManager.info(message);
    }

    public static void pass(String message) {
        log.info(message);
        ExtentTestManager.pass(message);
    }

    public static void fail(String message) {
        log.error(message);
        ExtentTestManager.fail(message);
    }

    public static void warn(String message) {
        log.warn(message);
        ExtentTestManager.warning(message);
    }
}
