package com.automation.framework.utilities;

import com.automation.framework.core.config.ConfigurationManager;
import com.automation.framework.core.driver.DriverManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;

/**
 * Captures screenshots to disk (for artifacts) and as Base64 (for embedding in
 * the HTML report). Never throws into a test: a failed capture is logged and
 * degrades gracefully.
 */
public final class ScreenshotUtils {

    private static final Logger log = LogManager.getLogger(ScreenshotUtils.class);
    private static final DateTimeFormatter TS = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss_SSS");

    private ScreenshotUtils() {
    }

    /**
     * Save a screenshot to the configured screenshot directory.
     *
     * @return absolute path of the saved file, or {@code null} on failure.
     */
    public static String capture(String name) {
        if (!DriverManager.hasDriver()) {
            return null;
        }
        try {
            WebDriver driver = DriverManager.getDriver();
            File src = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            String dir = ConfigurationManager.getInstance().getScreenshotPath();
            Path target = Path.of(dir, sanitize(name) + "_" + LocalDateTime.now().format(TS) + ".png");
            Files.createDirectories(target.getParent());
            Files.copy(src.toPath(), target);
            log.info("Saved screenshot: {}", target.toAbsolutePath());
            return target.toAbsolutePath().toString();
        } catch (Exception e) {
            log.warn("Failed to capture screenshot '{}': {}", name, e.getMessage());
            return null;
        }
    }

    /** Base64-encoded PNG suitable for embedding directly into the report. */
    public static String captureBase64() {
        if (!DriverManager.hasDriver()) {
            return null;
        }
        try {
            WebDriver driver = DriverManager.getDriver();
            byte[] bytes = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
            return Base64.getEncoder().encodeToString(bytes);
        } catch (Exception e) {
            log.warn("Failed to capture Base64 screenshot: {}", e.getMessage());
            return null;
        }
    }

    private static String sanitize(String name) {
        return name == null ? "screenshot" : name.replaceAll("[^a-zA-Z0-9-_]", "_");
    }
}
