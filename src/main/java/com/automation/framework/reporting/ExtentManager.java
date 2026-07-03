package com.automation.framework.reporting;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;
import com.automation.framework.core.config.ConfigurationManager;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Owns the single {@link ExtentReports} instance for a test run and writes the
 * HTML report to the configured report directory.
 */
public final class ExtentManager {

    private static volatile ExtentReports extent;

    private ExtentManager() {
    }

    public static ExtentReports getInstance() {
        if (extent == null) {
            synchronized (ExtentManager.class) {
                if (extent == null) {
                    extent = create();
                }
            }
        }
        return extent;
    }

    private static ExtentReports create() {
        ConfigurationManager config = ConfigurationManager.getInstance();
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String reportFile = config.getReportPath() + "/report_" + timestamp + ".html";

        ExtentSparkReporter spark = new ExtentSparkReporter(reportFile);
        spark.config().setDocumentTitle(config.getReportName());
        spark.config().setReportName(config.getReportName());
        spark.config().setTheme(Theme.STANDARD);

        ExtentReports reports = new ExtentReports();
        reports.attachReporter(spark);
        reports.setSystemInfo("Application", config.getApplicationName());
        reports.setSystemInfo("Environment", config.getEnvironmentName());
        reports.setSystemInfo("Base URL", safe(config::getBaseUrl));
        reports.setSystemInfo("Browser", config.getBrowser().name());
        reports.setSystemInfo("Headless", String.valueOf(config.isHeadless()));
        reports.setSystemInfo("Remote", String.valueOf(config.isRemoteExecution()));
        return reports;
    }

    public static void flush() {
        if (extent != null) {
            extent.flush();
        }
    }

    private static String safe(java.util.function.Supplier<String> supplier) {
        try {
            return supplier.get();
        } catch (RuntimeException e) {
            return "n/a";
        }
    }
}
