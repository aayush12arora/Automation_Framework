package com.automation.framework.core.driver;

import com.automation.framework.exceptions.FrameworkException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;

/**
 * Holds one {@link WebDriver} per thread so that tests can run in parallel
 * safely. Tests never {@code new} a driver directly; they call
 * {@link #initDriver()} / {@link #getDriver()} / {@link #quitDriver()}.
 */
public final class DriverManager {

    private static final Logger log = LogManager.getLogger(DriverManager.class);
    private static final ThreadLocal<WebDriver> DRIVER = new ThreadLocal<>();

    private DriverManager() {
    }

    /** Create and register a driver for the current thread. */
    public static WebDriver initDriver() {
        if (DRIVER.get() != null) {
            log.warn("A driver already exists on this thread; quitting it before creating a new one");
            quitDriver();
        }
        DRIVER.set(DriverFactory.create());
        return DRIVER.get();
    }

    /** Get the driver for the current thread, or fail if none was initialised. */
    public static WebDriver getDriver() {
        WebDriver driver = DRIVER.get();
        if (driver == null) {
            throw new FrameworkException(
                    "No WebDriver on this thread. Call DriverManager.initDriver() first "
                            + "(BaseUITest does this automatically in @BeforeMethod).");
        }
        return driver;
    }

    public static boolean hasDriver() {
        return DRIVER.get() != null;
    }

    /** Quit and remove the current thread's driver. Safe to call when none exists. */
    public static void quitDriver() {
        WebDriver driver = DRIVER.get();
        if (driver != null) {
            try {
                driver.quit();
            } catch (RuntimeException e) {
                log.warn("Error while quitting driver: {}", e.getMessage());
            } finally {
                DRIVER.remove();
            }
        }
    }
}
