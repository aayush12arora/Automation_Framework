package com.automation.framework.reporting;

import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.MediaEntityBuilder;
import com.aventstack.extentreports.Status;

/**
 * Thread-safe access to the current test's {@link ExtentTest} node so that
 * parallel tests log to their own report entry. The framework logs through
 * these static helpers; tests may also call them directly.
 */
public final class ExtentTestManager {

    private static final ThreadLocal<ExtentTest> CURRENT = new ThreadLocal<>();

    private ExtentTestManager() {
    }

    public static ExtentTest startTest(String name, String description) {
        ExtentTest test = ExtentManager.getInstance().createTest(name, description);
        CURRENT.set(test);
        return test;
    }

    public static ExtentTest getTest() {
        return CURRENT.get();
    }

    public static void remove() {
        CURRENT.remove();
    }

    // -------------------------------------------------------- convenience logging

    public static void info(String message) {
        if (getTest() != null) {
            getTest().info(message);
        }
    }

    public static void pass(String message) {
        if (getTest() != null) {
            getTest().pass(message);
        }
    }

    public static void fail(String message) {
        if (getTest() != null) {
            getTest().fail(message);
        }
    }

    public static void warning(String message) {
        if (getTest() != null) {
            getTest().warning(message);
        }
    }

    public static void skip(String message) {
        if (getTest() != null) {
            getTest().skip(message);
        }
    }

    /** Attach a Base64 PNG screenshot to the current report node. */
    public static void attachScreenshot(Status status, String message, String base64Png) {
        if (getTest() == null) {
            return;
        }
        if (base64Png == null) {
            getTest().log(status, message);
            return;
        }
        getTest().log(status, message,
                MediaEntityBuilder.createScreenCaptureFromBase64String(base64Png).build());
    }
}
